package com.abc.dddtemplate.adapter.domain.event;

import com.alibaba.fastjson.JSON;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.aggregates.Event;
import com.abc.dddtemplate.convention.UnitOfWork;
import com.abc.dddtemplate.share.exception.ErrorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

/**
 * 领域事件发布适配配置
 *
 * @author <template/>
 * @date
 */
@Slf4j
@Configuration
@ConditionalOnProperty(
        prefix = "rocketmq",
        value = {"name-server", "producer.group"}
)
public class IntergrationEventPublisherRocketMQAdapterConfig {
    private final RocketMQTemplate rocketMQTemplate;
    private final AggregateRepository<Event, Long> eventRepository;

    /**
     * 如下配置需配置好，保障RocketMqTemplate被初始化
     * ## rocketmq
     * #rocketmq.name-server = myrocket.nameserver:9876
     * #rocketmq.producer.group=${spring.application.name}
     *
     * @param rocketMQTemplate
     */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public IntergrationEventPublisherRocketMQAdapterConfig(
            @Autowired(required = false) RocketMQTemplate rocketMQTemplate,
            @Autowired(required = false) AggregateRepository<Event, Long> eventRepository
    ) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.eventRepository = eventRepository;
    }

    /**
     * Spring事务事件监听
     *
     * @param transactionCommittedEvent
     */
    @TransactionalEventListener(fallbackExecution = true, classes = UnitOfWork.TransactionCommittedEvent.class)
    public void publishEventByRocketMqAfterTransactionCommit(UnitOfWork.TransactionCommittedEvent transactionCommittedEvent) {
        List<Event> events = transactionCommittedEvent.getEvents();
        if (CollectionUtils.isEmpty(events)) {
            return;
        }
        if (rocketMQTemplate == null) {
            for (Event event : events) {
                event.comfirmedDelivered();
                eventRepository.save(event);
            }
            return;
            //throw new ErrorException("RocketMq未配置，无法发送集成事件！");
        }
        for (Event event : events) {
            try {
                String destination = event.getEventType();
                rocketMQTemplate.asyncSend(destination, event.restorePayload(), new DomainEventSendCallback(event, eventRepository));
            } catch (Exception ex) {
                log.error("集成事件发布失败", ex);
            }
        }
    }

    @Slf4j
    public static class DomainEventSendCallback implements SendCallback {
        private Event event;
        private final AggregateRepository<Event, Long> eventRepository;

        public DomainEventSendCallback(Event event, AggregateRepository<Event, Long> eventRepository) {
            this.event = event;
            this.eventRepository = eventRepository;
        }

        @Override
        public void onSuccess(SendResult sendResult) {
            // 修改事件消费状态
            if (event == null) {
                throw new ErrorException("集成事件不存在 event = " + event.toString());
            }
            try {
                event.comfirmedDelivered();
                eventRepository.save(event);
                log.info(String.format("集成事件发送成功, destination=%s, body=%s", event.getEventType(), JSON.toJSONString(event.restorePayload())));
            } catch (Exception ex) {
                log.error("本地事件库持久化失败", ex);
            }
        }

        @Override
        public void onException(Throwable throwable) {
            if (event == null) {
                throw new ErrorException("集成事件不存在 event = " + event.toString());
            }
            try {
                log.error(String.format("集成事件发送失败, destination=%s, body=%s", event.getEventType(), JSON.toJSONString(event.restorePayload())), throwable);
            } catch (Exception ex) {
                log.error("本地事件库持久化失败", ex);
            }
        }
    }
}
