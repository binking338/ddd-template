package com.abc.dddtemplate.application.subscribers.internal;


import com.abc.dddtemplate.domain.aggregates.samples.Order;
import com.abc.dddtemplate.domain.events.BillPaidDomainEvent;
import com.abc.dddtemplate.share.exception.ErrorException;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.DomainEventSubscriber;
import com.abc.dddtemplate.convention.UnitOfWork;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 账单支付领域事件
 * 领域事件处理逻辑，后缀可命名为Subscriber或Handler
 */
@Service
@RequiredArgsConstructor
public class BillPaiedDomainEventSubscriber4Order implements DomainEventSubscriber<BillPaidDomainEvent> {
    private final AggregateRepository<Order, Long> orderRepository;

    @Override
    public Class<BillPaidDomainEvent> forEventClass() {
        return BillPaidDomainEvent.class;
    }

    @Override
    public void onEvent(BillPaidDomainEvent event) {
        Order order = orderRepository.findById(event.getBill().getOrderId())
                .orElseThrow(()->new ErrorException("订单丢失"));
        order.finish();
        UnitOfWork.saveTransactional(()->{
            orderRepository.save(order);
        });

    }
}