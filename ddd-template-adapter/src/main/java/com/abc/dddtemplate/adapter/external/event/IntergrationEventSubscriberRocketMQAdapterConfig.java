package com.abc.dddtemplate.adapter.external.event;

import com.abc.dddtemplate.convention.DomainEventSupervisor;
import com.abc.dddtemplate.share.annotation.DomainEvent;
import com.abc.dddtemplate.share.util.ScanUtils;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 自动监听集成事件对应的RocketMQ
 *
 * @author <template/>
 * @date 2023-02-28
 */
@Slf4j
@Configuration
@ConditionalOnProperty(
        prefix = "rocketmq",
        value = {"name-server"}
)
public class IntergrationEventSubscriberRocketMQAdapterConfig {
    @Value("${spring.application.name:default}")
    String applicationName;
    @Value("${rocketmq.name-server:}")
    String defaultNameSrv;
    @Autowired
    Environment environment;
    @Autowired
    DomainEventSupervisor domainEventSupervisor;

    List<MQPushConsumer> mqPushConsumers = new ArrayList<>();

    @PostConstruct
    public void init() {
        Set<Class<?>> classes = ScanUtils.scanClass("com.abc.dddtemplate.external", true);
        classes.stream().filter(cls -> {
            DomainEvent domainEvent = cls.getAnnotation(DomainEvent.class);
            if (!Objects.isNull(domainEvent) && StringUtils.isNotEmpty(domainEvent.value())) {
                return true;
            } else {
                return false;
            }
        }).forEach(domainEventClass -> {
            MQPushConsumer mqPushConsumer = startConsuming(domainEventClass);
            if (mqPushConsumer != null) {
                mqPushConsumers.add(mqPushConsumer);
            }
        });
    }

    @EventListener(classes = ContextClosedEvent.class)
    public void shutdown(ContextClosedEvent event) {
        if (CollectionUtils.isEmpty(mqPushConsumers)) {
            return;
        }
        mqPushConsumers.forEach(mqPushConsumer -> {
            mqPushConsumer.shutdown();
        });
    }

    private DefaultMQPushConsumer startConsuming(Class domainEventClass) {
        DomainEvent domainEvent = (DomainEvent) domainEventClass.getAnnotation(DomainEvent.class);
        if (Objects.isNull(domainEvent) || StringUtils.isBlank(domainEvent.value())) {
            // 不是集成事件
            return null;
        }
        String target = domainEvent.value();
        String topic = target.lastIndexOf(':') > 0 ? target.substring(0, target.lastIndexOf(':') - 1) : target;
        String tag = target.lastIndexOf(':') > 0 ? target.substring(target.lastIndexOf(':') + 1) : "";

        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        try {
            mqPushConsumer.setConsumerGroup(getTopicConsumerGroup(topic));
            mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            mqPushConsumer.setInstanceName(applicationName);
            mqPushConsumer.subscribe(topic, tag);
            String nameServerAddr = getTopicNamesrvAddr(topic);
            mqPushConsumer.setNamesrvAddr(nameServerAddr);
            mqPushConsumer.setUnitName(domainEventClass.getSimpleName());
            mqPushConsumer.registerMessageListener((List<MessageExt> msgs, ConsumeConcurrentlyContext context) -> {
                        try {
                            for (MessageExt msg :
                                    msgs) {
                                String strMsg = new String(msg.getBody(), "UTF-8");
                                Object event = JSON.parseObject(strMsg, domainEventClass);
                                domainEventSupervisor.dispatchRawImmediately(event, true);
                            }
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        } catch (Exception ex) {
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                    }
            );
            mqPushConsumer.start();
        } catch (MQClientException e) {
            log.error("领域事件消息监听启动失败", e);
        }
        return mqPushConsumer;
    }

    private String getTopicConsumerGroup(String topic) {
        String group = environment.getProperty("rocketmq." + topic + ".consumer.group", topic + "-4-" + applicationName);
        return group;
    }

    private String getTopicNamesrvAddr(String topic) {
        String nameServer = environment.getProperty("rocketmq." + topic + ".name-server", defaultNameSrv);
        return nameServer;
    }
}
