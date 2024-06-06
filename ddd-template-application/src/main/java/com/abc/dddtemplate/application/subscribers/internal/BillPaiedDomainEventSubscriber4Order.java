package com.abc.dddtemplate.application.subscribers.internal;

import com.abc.dddtemplate.application.commands.order.CompleteOrderCmd;
import com.abc.dddtemplate.domain.aggregates.samples.events.external.BillPaidDomainEvent;
//import com.abc.dddtemplate.convention.DomainEventSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * 账单支付领域事件
 * 领域事件处理逻辑，后缀可命名为Subscriber或Handler
 */
@Service
@RequiredArgsConstructor
public class BillPaiedDomainEventSubscriber4Order{
    private final CompleteOrderCmd.Handler completedOrderCmdHandler;

    @EventListener(classes = BillPaidDomainEvent.class)
    public void onEvent(BillPaidDomainEvent event) {
        completedOrderCmdHandler.exec(CompleteOrderCmd.builder().orderId(event.getBill().getOrderId()).build());
    }
}
//@Service
//@RequiredArgsConstructor
//public class BillPaiedDomainEventSubscriber4Order implements DomainEventSubscriber<BillPaidDomainEvent> {
//    private final CompleteOrderCmd.Handler completedOrderCmdHandler;
//
//    @Override
//    public Class<BillPaidDomainEvent> forEventClass() {
//        return BillPaidDomainEvent.class;
//    }
//
//    @Override
//    public void onEvent(BillPaidDomainEvent event) {
//        completedOrderCmdHandler.exec(CompleteOrderCmd.builder().orderId(event.getBill().getOrderId()).build());
//    }
//}