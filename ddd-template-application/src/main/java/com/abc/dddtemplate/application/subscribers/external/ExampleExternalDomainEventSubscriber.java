package com.abc.dddtemplate.application.subscribers.external;

import com.abc.dddtemplate.convention.DomainEventSubscriber;
import com.abc.dddtemplate.domain.events.external.ExampleExternalDomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 领域事件处理逻辑，后缀可命名为Subscriber或Handler
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExampleExternalDomainEventSubscriber implements DomainEventSubscriber<ExampleExternalDomainEvent> {

    @Override
    public Class<ExampleExternalDomainEvent> forEventClass() {
        return ExampleExternalDomainEvent.class;
    }

    @Override
    public void onEvent(ExampleExternalDomainEvent event) {
        // 事件订阅逻辑
        log.info(event.getMsg());
    }
}
