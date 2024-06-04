package com.abc.dddtemplate.domain.aggregates.events.internal;

import com.abc.dddtemplate.domain.aggregates.samples.Order;
import com.abc.dddtemplate.share.annotation.DomainEvent;
import lombok.*;

/**
 * 领域事件 - 新订单创建
 * @author <template/>
 * @date
 */
@DomainEvent
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPlacedDomainEvent {
    private Order order;
}
