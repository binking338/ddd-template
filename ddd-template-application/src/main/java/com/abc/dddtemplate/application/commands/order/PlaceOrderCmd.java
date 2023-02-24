package com.abc.dddtemplate.application.commands.order;

import com.abc.dddtemplate.application.sagas.PlaceOrderSagaService;
import com.abc.dddtemplate.convention.Command;
import com.abc.dddtemplate.convention.aggregates.Saga;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 下单
 * @author <template/>
 * @date
 */
@Service
@RequiredArgsConstructor
public class PlaceOrderCmd implements Command<PlaceOrderCmd.CreateOrderDTO, Long> {
    private final PlaceOrderSagaService placeOrderSagaService;

    @Override
    public Long exec(CreateOrderDTO dto) {
        Saga saga = placeOrderSagaService.run(PlaceOrderSagaService.Context.builder()
                .name(dto.name)
                .num(dto.num)
                .price(dto.price)
                .owner(dto.owner)
                .build());

        return saga.getContext(PlaceOrderSagaService.Context.class).getOrderId();
    }

    @Data
    public static class CreateOrderDTO {
        String owner;
        String name;
        Integer num;
        Integer price;
    }
}
