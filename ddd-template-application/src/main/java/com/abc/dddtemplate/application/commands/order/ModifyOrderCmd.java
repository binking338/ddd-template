package com.abc.dddtemplate.application.commands.order;

import com.abc.dddtemplate.convention.Command;
import com.abc.dddtemplate.domain.aggregates.samples.Order;
import com.abc.dddtemplate.domain.aggregates.samples.OrderItem;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.UnitOfWork;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 修改订单项
 *
 * @author <template/>
 * @date
 */
@Data
public class ModifyOrderCmd {
    Long orderId;
    String name;
    Integer num;
    Integer price;


    @Service
    @RequiredArgsConstructor
    public static class Handler implements Command<ModifyOrderCmd, Boolean> {
        private final AggregateRepository<Order, Long> orderRepository;
        private final UnitOfWork unitOfWork;

        @Override
        public Boolean exec(ModifyOrderCmd cmd) {
            Order order = unitOfWork.required(() -> orderRepository.findById(cmd.orderId).orElse(null));
            if (order == null) {
                return false;
            }
            List<OrderItem> items = order.clearItem();
            order.addItem(cmd.name, cmd.price, cmd.num);
            unitOfWork.save(order);
            return true;
        }
    }
}
