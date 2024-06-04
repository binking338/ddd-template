package com.abc.dddtemplate.application.sagas;

import com.abc.dddtemplate.application._share.clients.InventoryClient;
import com.abc.dddtemplate.application.commands.order.PlaceOrderCmd;
import com.abc.dddtemplate.convention.SagaStateMachine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 下单Saga服务，模拟扣减库存的需求
 *
 * @author <template/>
 * @date
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderSaga {
    String owner;
    String name;
    Integer num;
    Integer price;
    Long orderId;

    @Service
    @Slf4j
    public static class Handler extends SagaStateMachine<PlaceOrderSaga> {
        @Autowired
        InventoryClient inventoryClient;
        @Autowired
        PlaceOrderCmd placeOrderCmd;

        @Override
        protected Integer getBizType() {
            return 1001;
        }

        @Override
        protected Class<PlaceOrderSaga> getContextClass() {
            return PlaceOrderSaga.class;
        }

        @Override
        protected Process<PlaceOrderSaga> config() {
            return Process.of((PlaceOrderSaga context) -> {
                inventoryClient.reduce(context.name, context.num);
            }).then(context -> {
                placeOrderCmd.exec(PlaceOrderCmd.CreateOrderDTO.builder()
                        .owner(context.owner)
                        .name(context.name)
                        .price(context.price)
                        .num(context.num)
                        .build());
            }).root();
        }
    }
}
