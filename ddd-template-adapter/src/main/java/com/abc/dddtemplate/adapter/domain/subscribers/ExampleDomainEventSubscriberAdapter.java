package com.abc.dddtemplate.adapter.domain.subscribers;

import com.abc.dddtemplate.convention.DomainEventSupervisor;
import com.abc.dddtemplate.application.subscribers.external.ExampleExternalDomainEventSubscriber.ExampleExternalDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.abc.dddtemplate.share.Constants.ACL_DOMAIN_EVENT_EXAMPLE_EXTERNAL;
import static com.abc.dddtemplate.share.Constants.ACL_DOMAIN_EVENT_EXAMPLE_EXTERNAL_CONSUMER_GROUP;

/**
 * 消费领域事件业务逻辑实现的地方，核心功能来源于domain层。
 * @author <template/>
 * @date
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = ACL_DOMAIN_EVENT_EXAMPLE_EXTERNAL, consumerGroup = ACL_DOMAIN_EVENT_EXAMPLE_EXTERNAL_CONSUMER_GROUP)
public class ExampleDomainEventSubscriberAdapter implements RocketMQListener<ExampleExternalDomainEvent> {
    @Autowired
    DomainEventSupervisor domainEventSupervisor;

    @Override
    public void onMessage(ExampleExternalDomainEvent event) {
        domainEventSupervisor.dispatchRawImmediately(event);
    }
}
