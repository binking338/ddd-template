package com.abc.dddtemplate.convention;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <template/>
 * @date
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventScheduleService {

    @Value("${schedule.event.enabled:false}")
    private boolean enabled;

    private final AggregateRepository<Event, Long> eventRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final LockerService lockerService;

    private boolean compensationRunning = false;

    /**
     * 本地事件库补偿发送
     */
    @Scheduled(cron = "*/10 * * * * ?")
    public void compensation() {
        if (!enabled) {
            return;
        }
        if (compensationRunning) {
            log.info("上次任务仍未结束，跳过本次任务");
            return;
        }
        compensationRunning = true;

        String pwd = RandomStringUtils.random(8, true, true);
        String locker = "event_compensation";
        Duration lockDuration = Duration.ofSeconds(30);
        try {
            boolean noneEvent = false;
            boolean existConcurrency = false;
            while (!noneEvent) {
                try {
                    if (!lockerService.acquire(locker, pwd, lockDuration)) {
                        return;
                    }
                    Page<Event> events = eventRepository.findAll((root, cq, cb) -> {
                        Date now = new Date();
                        cq.where(cb.or(
                                cb.and(
                                        // 【初始状态】
                                        cb.equal(root.get("eventState"), Event.EventState.INIT),
                                        cb.lessThan(root.get("nextTryTime"), now)
                                ), cb.and(
                                        // 【未知状态】
                                        cb.equal(root.get("eventState"), Event.EventState.COMFIRMING),
                                        cb.lessThan(root.get("nextTryTime"), now)
                                )));
                        return null;
                    }, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createAt")));
                    if (!events.hasContent()) {
                        noneEvent = true;
                        break;
                    }

                    List<Event> retryEvents = new ArrayList<>(10);
                    try {
                        for (Event event : events) {
                            Date now = new Date();
                            if (!event.tryDelivery(now)) {
                                eventRepository.save(event);
                                continue;
                            } else {
                                event = eventRepository.saveAndFlush(event);
                                retryEvents.add(event);
                            }
                        }
                    } catch (Exception ex) {
                        // 数据库并发异常
                        existConcurrency = true;
                        log.error("集成事件补偿发送-持久化失败", ex);
                    }


                    UnitOfWork.TransactionCommittedEvent transactionCommittedEvent = new UnitOfWork.TransactionCommittedEvent(this, retryEvents);
                    applicationEventPublisher.publishEvent(transactionCommittedEvent);
                    if (existConcurrency) {
                        log.info("集成事件补偿发送-并发退出");
                        break;
                    }
                } catch (Exception ex) {
                    log.error("集成事件补偿发送-失败", ex);
                }
            }
        } finally {
            compensationRunning = false;
            lockerService.release(locker, pwd);
        }
    }

    /**
     * 本地事件库归档
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void archiving() {
        if (!enabled) {
            return;
        }
        String pwd = RandomStringUtils.random(8, true, true);
        String locker = "event_archiving";
        Duration lockDuration = Duration.ofHours(3);

        Date now = new Date();
        boolean noneEvent = false;
        int failCount = 0;
        while (!noneEvent) {
            try {
                if (!lockerService.acquire(locker, pwd, lockDuration)) {
                    return;
                }
                Page<Event> events = eventRepository.findAll((root, cq, cb) -> {
                    cq.where(
                            cb.and(
                                    // 【状态】
                                    cb.or(
                                            cb.equal(root.get("eventState"), Event.EventState.CANCEL),
                                            cb.equal(root.get("eventState"), Event.EventState.EXPIRED),
                                            cb.equal(root.get("eventState"), Event.EventState.FAILED),
                                            cb.equal(root.get("eventState"), Event.EventState.DELIVERED)
                                    ),
                                    cb.lessThan(root.get("expireAt"), DateUtils.addDays(now, -7))
                            ));
                    return null;
                }, PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "createAt")));
                if (!events.hasContent()) {
                    noneEvent = true;
                    break;
                }
                List<ArchivedEvent> archivedEvents = events.stream().map(e -> ArchivedEvent.builder()
                        .id(e.getId())
                        .dataType(e.getDataType())
                        .data(e.getData())
                        .eventType(e.getEventType())
                        .eventState(e.getEventState())
                        .createAt(e.getCreateAt())
                        .expireAt(e.getExpireAt())
                        .nextTryTime(e.getNextTryTime())
                        .lastTryTime(e.getLastTryTime())
                        .tryTimes(e.getTryTimes())
                        .triedTimes(e.getTriedTimes())
                        .version(e.getVersion())
                        .build()
                ).collect(Collectors.toList());
                UnitOfWork.saveEntities(archivedEvents, events.toList());
            } catch (Exception ex) {
                failCount++;
                log.error("集成事件归档-失败", ex);
                if (failCount >= 3) {
                    log.info("集成事件归档-累计3次退出任务");
                    break;
                }
            }
        }
        lockerService.release(locker, pwd);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void addPartition() {
        if(!enabled){
            return;
        }
        Date now  = new Date();
        addPartition("__event", DateUtils.addMonths(now, 1));
        addPartition("__archived_event", DateUtils.addMonths(now, 1));
    }


    private final JdbcTemplate jdbcTemplate;
    private void addPartition(String table, Date date){
        String sql = "alter table `" + table + "` add partition (partition p" + DateFormatUtils.format(date, "yyyyMM") + " values less than (to_days('" + DateFormatUtils.format(DateUtils.addMonths(date, 1), "yyyy-MM") + "-01')) ENGINE=InnoDB)";
        try{
            jdbcTemplate.execute(sql);
        } catch (Exception ex){
            if(!ex.getMessage().contains("Duplicate partition")) {
                log.error("分区创建异常 table = " + table + " partition = p" + DateFormatUtils.format(date, "yyyyMM"), ex);
            }
        }
    }
}
