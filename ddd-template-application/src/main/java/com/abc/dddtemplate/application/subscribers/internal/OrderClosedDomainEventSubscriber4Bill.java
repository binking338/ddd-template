package com.abc.dddtemplate.application.subscribers.internal;

import com.abc.dddtemplate.application.commands.bill.CloseBillCmd;
import com.abc.dddtemplate.convention.schemas.BillSchema;
import com.abc.dddtemplate.domain.aggregates.samples.Bill;
import com.abc.dddtemplate.domain.events.internal.OrderClosedDomainEvent;
import com.abc.dddtemplate.share.exception.ErrorException;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.DomainEventSubscriber;
import com.abc.dddtemplate.convention.UnitOfWork;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 订单关闭领域事件消费者
 * 领域事件处理逻辑，后缀可命名为Subscriber或Handler
 */
@Service
@RequiredArgsConstructor
public class OrderClosedDomainEventSubscriber4Bill implements DomainEventSubscriber<OrderClosedDomainEvent> {
    private final CloseBillCmd.Handler handler;

    @Override
    public Class<OrderClosedDomainEvent> forEventClass() {
        return OrderClosedDomainEvent.class;
    }

    @Override
    public void onEvent(OrderClosedDomainEvent event) {
        handler.exec(CloseBillCmd.builder()
                .orderId( event.getOrder().getId())
                .build());
    }
}