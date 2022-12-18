package com.abc.dddtemplate.application.subscribers.internal;

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
    private final AggregateRepository<Bill, Long> billRepository;
    private final UnitOfWork unitOfWork;

    @Override
    public Class<OrderClosedDomainEvent> forEventClass() {
        return OrderClosedDomainEvent.class;
    }

    @Override
    public void onEvent(OrderClosedDomainEvent event) {
        Bill bill = billRepository.findOne(BillSchema.specify(b -> b.orderId().eq(event.getOrder().getId())))
                .orElseThrow(() -> new ErrorException("账单丢失"));
        bill.close();
        UnitOfWork.saveTransactional(() -> {
            billRepository.save(bill);
            // throw new WarnException("测试UnitOfWork的静态方法实现");
            return null;
        });
    }
}