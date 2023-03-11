package com.abc.dddtemplate.application.commands.order;

import com.abc.dddtemplate.convention.Command;
import com.abc.dddtemplate.convention.UnitOfWork;
import com.abc.dddtemplate.domain.aggregates.samples.Order;
import lombok.*;
import org.springframework.stereotype.Service;

/**
 * 下单
 *
 * @author <template/>
 * @date
 */
@Service
@RequiredArgsConstructor
public class PlaceOrderCmd implements Command<PlaceOrderCmd.CreateOrderDTO, Long> {

    @Override
    public Long exec(CreateOrderDTO dto) {
        Order order = Order.placeOrder(dto.owner, dto.name, dto.price, dto.num);
        UnitOfWork.saveEntities(order);
        return order.getId();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOrderDTO {
        String owner;
        String name;
        Integer num;
        Integer price;
    }
}
