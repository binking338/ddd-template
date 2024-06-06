package com.abc.dddtemplate.application.commands.order;

import com.abc.dddtemplate.application._share.clients.InventoryClient;
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderCmd{
    String owner;
    String name;
    Integer num;
    Integer price;

    @Service
    @RequiredArgsConstructor
    public static class Handler implements Command<PlaceOrderCmd, Long> {
        private final InventoryClient inventoryClient;
        @Override
        public Long exec(PlaceOrderCmd cmd) {
            Order order = Order.placeOrder(cmd.owner, cmd.name, cmd.price, cmd.num);
            UnitOfWork.saveEntities(order);
            return order.getId();
        }
    }
}
