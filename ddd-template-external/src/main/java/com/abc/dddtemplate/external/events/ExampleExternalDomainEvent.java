package com.abc.dddtemplate.external.events;


import com.abc.dddtemplate.share.Constants;
import com.abc.dddtemplate.share.annotation.DomainEvent;
import lombok.Data;

/**
 * 声明外部服务的领域事件（集成事件）
 * 该类型定义领域事件的消息体结构
 *
 * @author <template/>
 * @date
 */
@Data
@DomainEvent(Constants.ACL_DOMAIN_EVENT_EXAMPLE_EXTERNAL)
public class ExampleExternalDomainEvent {

    // 消息体 属性成员定义
    String msg;
}
