package com.abc.dddtemplate.application.subscribers.internal;

import com.abc.dddtemplate.application.commands.order.CompleteOrderCmd;
import com.abc.dddtemplate.domain.events.BillPaidDomainEvent;
import com.abc.dddtemplate.convention.DomainEventSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 账单支付领域事件
 * 领域事件处理逻辑，后缀可命名为Subscriber或Handler
 */
@Service
@RequiredArgsConstructor
public class BillPaiedDomainEventSubscriber4Order implements DomainEventSubscriber<BillPaidDomainEvent> {
    private final CompleteOrderCmd.Handler completedOrderCmdHandler;

    @Override
    public Class<BillPaidDomainEvent> forEventClass() {
        return BillPaidDomainEvent.class;
    }

    @Override
    public void onEvent(BillPaidDomainEvent event) {
        completedOrderCmdHandler.exec(CompleteOrderCmd.builder().orderId(event.getBill().getOrderId()).build());
    }
}