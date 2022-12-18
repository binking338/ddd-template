package com.abc.dddtemplate.application.subscribers.external;

import com.abc.dddtemplate.share.Constants;
import com.abc.dddtemplate.share.annotation.DomainEvent;
import com.abc.dddtemplate.convention.DomainEventSubscriber;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 领域事件处理逻辑，后缀可命名为Subscriber或Handler
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExampleExternalDomainEventSubscriber implements DomainEventSubscriber<ExampleExternalDomainEventSubscriber.ExampleExternalDomainEvent> {

    @Override
    public Class<ExampleExternalDomainEvent> forEventClass() {
        return ExampleExternalDomainEvent.class;
    }

    @Override
    public void onEvent(ExampleExternalDomainEvent event) {
        // 事件订阅逻辑
        log.info(event.getMsg());
    }


    /**
     * 声明外部服务的领域事件（集成事件）
     * 该类型定义领域事件的消息体结构
     *
     * @author <template/>
     * @date
     */
    @Data
    @DomainEvent(Constants.ACL_DOMAIN_EVENT_EXAMPLE_EXTERNAL)
    public static class ExampleExternalDomainEvent {

        // 消息体 属性成员定义
        String msg;
    }
}
