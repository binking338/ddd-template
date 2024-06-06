package com.abc.dddtemplate.domain.aggregates.samples.events.internal;

import com.abc.dddtemplate.domain.aggregates.samples.Order;
import com.abc.dddtemplate.share.annotation.DomainEvent;
import lombok.*;

/**
 * 领域事件 - 订单关闭
 * @author <template/>
 * @date
 */
@DomainEvent
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderClosedDomainEvent {

    private Order order;

}
