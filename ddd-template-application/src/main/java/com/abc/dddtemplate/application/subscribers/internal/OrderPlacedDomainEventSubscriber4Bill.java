package com.abc.dddtemplate.application.subscribers.internal;

import com.abc.dddtemplate.application.commands.bill.BillingCmd;
import com.abc.dddtemplate.domain.aggregates.events.internal.OrderPlacedDomainEvent;
import com.abc.dddtemplate.convention.DomainEventSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 下订单事件
 */
@Service
@RequiredArgsConstructor
public class OrderPlacedDomainEventSubscriber4Bill implements DomainEventSubscriber<OrderPlacedDomainEvent> {
    private final BillingCmd.Handler handler;

    @Override
    public Class<OrderPlacedDomainEvent> forEventClass() {
        return OrderPlacedDomainEvent.class;
    }

    @Override
    public void onEvent(OrderPlacedDomainEvent event) {
        handler.exec(BillingCmd.builder()
                .orderId(event.getOrder().getId())
                .name(event.getOrder().getName())
                .amount(event.getOrder().getAmount())
                .owner(event.getOrder().getOwner())
        .build());
    }
}
