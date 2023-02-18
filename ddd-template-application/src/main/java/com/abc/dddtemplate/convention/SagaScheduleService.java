package com.abc.dddtemplate.convention;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
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
public class SagaScheduleService {

    @Value("${schedule.saga.enabled:false}")
    private boolean enabled;
    @Value("${app.id:default}")
    private String svcName;

    protected final AggregateRepository<Saga, Long> sagaRepository;
    protected final SagaSupervisor sagaSupervisor;
    private final LockerService lockerService;

    private boolean compensationRunning = false;
    private boolean rollbackingRunning = false;

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
        String locker = "saga_compensation[" + svcName + "]";
        try {
            boolean noneSaga = false;
            if (CollectionUtils.isEmpty(sagaSupervisor.getSupportedBizTypes())) {
                return;
            }
            while (!noneSaga) {
                if (!lockerService.acquire(locker, pwd, Duration.ofMinutes(5))) {
                    return;
                }
                Date now = new Date();
                Page<Saga> sagas = sagaRepository.findAll((root, cq, cb) -> {
                    cq.where(cb.or(
                            cb.and(
                                    // 【初始状态】
                                    cb.equal(root.get("sagaState"), Saga.SagaState.INIT),
                                    cb.lessThan(root.get("nextTryTime"), now),
                                    root.get("bizType").in(sagaSupervisor.getSupportedBizTypes())
                            ), cb.and(
                                    // 【未知状态】
                                    cb.equal(root.get("sagaState"), Saga.SagaState.RUNNING),
                                    cb.lessThan(root.get("nextTryTime"), now),
                                    root.get("bizType").in(sagaSupervisor.getSupportedBizTypes())
                            )));
                    return null;
                }, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createAt")));
                if (!sagas.hasContent()) {
                    noneSaga = true;
                    break;
                }
                for (Saga saga : sagas.toList()) {
                    sagaSupervisor.resume(saga);
                }
            }
        } catch (Exception ex) {
            log.error("saga事务补偿-失败", ex);
        } finally {
            compensationRunning = false;
            lockerService.release(locker, pwd);
        }
    }

    @Scheduled(cron = "*/10 * * * * ?")
    public void rollbacking() {
        if (!enabled) {
            return;
        }
        if (rollbackingRunning) {
            log.info("上次任务仍未结束，跳过本次任务");
            return;
        }
        rollbackingRunning = true;
        String pwd = RandomStringUtils.random(8, true, true);
        String locker = "saga_rollbacking[" + svcName + "]";
        try {
            boolean noneSaga = false;
            if (CollectionUtils.isEmpty(sagaSupervisor.getSupportedBizTypes())) {
                return;
            }
            while (!noneSaga) {
                if (!lockerService.acquire(locker, pwd, Duration.ofMinutes(5))) {
                    return;
                }
                Date now = new Date();
                Page<Saga> sagas = sagaRepository.findAll((root, cq, cb) -> {
                    cq.where(
                            cb.and(
                                    // 【回滚状态】
                                    cb.equal(root.get("sagaState"), Saga.SagaState.ROLLBACKING),
                                    root.get("bizType").in(sagaSupervisor.getSupportedBizTypes())

                            ));
                    return null;
                }, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "createAt")));
                if (!sagas.hasContent()) {
                    noneSaga = true;
                    break;
                }

                for (Saga saga : sagas.toList()) {
                    sagaSupervisor.rollback(saga);
                }
            }
        } catch (Exception ex) {
            log.error("saga事务回滚-失败", ex);
        } finally {
            rollbackingRunning = false;
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

        Date now = new Date();
        while (true) {
            try {
                if (!lockerService.acquire(locker, pwd, Duration.ofHours(1))) {
                    return;
                }
                Page<Saga> sagas = sagaRepository.findAll((root, cq, cb) -> {
                    cq.where(
                            cb.and(
                                    // 【状态】
                                    cb.or(
                                            cb.equal(root.get("sagaState"), Saga.SagaState.CANCEL),
                                            cb.equal(root.get("sagaState"), Saga.SagaState.EXPIRED),
                                            cb.equal(root.get("sagaState"), Saga.SagaState.FAILED),
                                            cb.equal(root.get("sagaState"), Saga.SagaState.ROLLBACKED),
                                            cb.equal(root.get("sagaState"), Saga.SagaState.DONE)
                                    ),
                                    cb.lessThan(root.get("expireAt"), DateUtils.addDays(now, 7)))
                    );
                    return null;
                }, PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "createAt")));
                if (!sagas.hasContent()) {
                    break;
                }
                List<ArchivedSaga> archivedSagas = sagas.stream().map(s -> ArchivedSaga.builder()
                        .id(s.getId())
                        .contextDataType(s.getContextDataType())
                        .contextData(s.getContextData())
                        .bizType(s.getBizType())
                        .svcName(s.getSvcName())
                        .sagaState(s.getSagaState())
                        .createAt(s.getCreateAt())
                        .expireAt(s.getExpireAt())
                        .nextTryTime(s.getNextTryTime())
                        .lastTryTime(s.getLastTryTime())
                        .tryTimes(s.getTryTimes())
                        .triedTimes(s.getTriedTimes())
                        .version(s.getVersion())
                        .processes(s.getProcesses().stream().map(p -> ArchivedSaga.SagaProcess.builder()
                                .id(p.getId())
                                .processCode(p.getProcessCode())
                                .processName(p.getProcessName())
                                .processState(p.getProcessState())
                                .contextData(p.getContextData())
                                .exception(p.getException())
                                .lastTryTime(p.getLastTryTime())
                                .triedTimes(p.getTriedTimes())
                                .createAt(p.getCreateAt())
                                .build()).collect(Collectors.toList()))
                        .build()
                ).collect(Collectors.toList());
                UnitOfWork.saveEntities(archivedSagas, sagas.toList());
            } catch (Exception ex) {
                log.error("Saga事务归档-失败", ex);
            }
        }
        lockerService.release(locker, pwd);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void addPartition() {
        if (!enabled) {
            return;
        }
        Date now = new Date();
        addPartition("__saga", DateUtils.addMonths(now, 1));
        addPartition("__saga_process", DateUtils.addMonths(now, 1));
        addPartition("__archived_saga", DateUtils.addMonths(now, 1));
        addPartition("__archived_saga_process", DateUtils.addMonths(now, 1));
    }

    private final JdbcTemplate jdbcTemplate;

    private void addPartition(String table, Date date) {
        String sql = "alter table `" + table + "` add partition (partition p" + DateFormatUtils.format(date, "yyyyMM") + " values less than (to_days('" + DateFormatUtils.format(DateUtils.addMonths(date, 1), "yyyy-MM") + "-01')) ENGINE=InnoDB)";
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception ex) {
            if (!ex.getMessage().contains("Duplicate partition")) {
                log.error("分区创建异常 table = " + table + " partition = p" + DateFormatUtils.format(date, "yyyyMM"), ex);
            }
        }
    }
}
