package com.abc.dddtemplate.application.subscribers.internal;


import com.abc.dddtemplate.domain.aggregates.samples.Bill;
import com.abc.dddtemplate.domain.events.internal.OrderPlacedDomainEvent;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.DomainEventSubscriber;
import com.abc.dddtemplate.convention.UnitOfWork;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderPlacedDomainEventSubscriber4Bill implements DomainEventSubscriber<OrderPlacedDomainEvent> {
    private final AggregateRepository<Bill, Long> billRepository;
    private final UnitOfWork unitOfWork;

    @Override
    public Class<OrderPlacedDomainEvent> forEventClass() {
        return OrderPlacedDomainEvent.class;
    }

    @Override
    public void onEvent(OrderPlacedDomainEvent event) {

        Bill bill = Bill.builder()
                .orderId(event.getOrder().getId())
                .name(event.getOrder().getName())
                .amount(event.getOrder().getAmount())
                .build();
        unitOfWork.save(bill);
//            unitOfWork.required(() -> {
//                billRepository.save(bill);
//                // throw new WarnException("测试UnitOfWork的实例方法实现");
//                return null;
//            });
    }
}
