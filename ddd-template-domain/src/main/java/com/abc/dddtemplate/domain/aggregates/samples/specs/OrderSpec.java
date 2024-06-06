package com.abc.dddtemplate.domain.aggregates.samples.specs;

import com.abc.dddtemplate.convention.Specification;
import com.abc.dddtemplate.domain.aggregates.samples.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 订单业务约束接口
 * @author <template/>
 * @date
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSpec implements Specification<Order> {
    @Override
    public Class<Order> entityClass() {
        return Order.class;
    }

    @Override
    public boolean inTransaction() {
        return false;
    }

    @Override
    public Result valid(Order order) {
        boolean passed = order.getOrderItems().size() > 0
                && order.getOrderItems().stream().mapToInt(i -> i.getPrice() * i.getNum()).sum() == order.getAmount();
        if(passed){
            return Result.pass();
        } else {
            return Result.fail("订单不符合要求");
        }
    }
}
