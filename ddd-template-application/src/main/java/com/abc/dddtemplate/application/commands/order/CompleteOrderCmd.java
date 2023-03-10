package com.abc.dddtemplate.application.commands.order;

import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.Command;
import com.abc.dddtemplate.convention.UnitOfWork;
import com.abc.dddtemplate.domain.aggregates.samples.Order;
import com.abc.dddtemplate.share.exception.ErrorException;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 完成订单
 * @author <template/>
 * @date 2023-03-10
 */
@Data
@Builder
public class CompleteOrderCmd {
    /**
     * 订单Id
     */
    private Long orderId;

    @Service
    @RequiredArgsConstructor
    public static class Handler implements Command<CompleteOrderCmd, Boolean> {
        private final AggregateRepository<Order, Long> orderRepository;

        @Override
        public Boolean exec(CompleteOrderCmd cmd) {
            Order order = orderRepository.findById(cmd.getOrderId())
                    .orElseThrow(() -> new ErrorException("订单丢失"));
            order.finish();
            UnitOfWork.saveTransactional(() -> {
                orderRepository.save(order);
            });
            return true;
        }
    }
}
