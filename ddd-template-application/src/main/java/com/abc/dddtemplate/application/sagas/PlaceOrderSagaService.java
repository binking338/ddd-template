package com.abc.dddtemplate.application.sagas;

import com.abc.dddtemplate.application.commands.order.PlaceOrderCmd;
import com.abc.dddtemplate.domain.aggregates.samples.Order;
import com.abc.dddtemplate.convention.SagaStateMachine;
import com.abc.dddtemplate.convention.UnitOfWork;
import com.abc.dddtemplate.external.clients.InventoryClient;
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
@Service
@Slf4j
public class PlaceOrderSagaService extends SagaStateMachine<PlaceOrderSagaService.Context> {
    @Autowired
    InventoryClient inventoryClient;
    @Autowired
    PlaceOrderCmd placeOrderCmd;

    @Override
    protected Integer getBizType() {
        return 1001;
    }

    @Override
    protected Class<Context> getContextClass() {
        return Context.class;
    }

    @Override
    protected Process<Context> config() {
        return Process.of((Context context) -> {
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Context {
        String owner;
        String name;
        Integer num;
        Integer price;
        Long orderId;
    }
}
