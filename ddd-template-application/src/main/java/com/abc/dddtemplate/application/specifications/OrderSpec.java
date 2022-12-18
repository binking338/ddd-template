package com.abc.dddtemplate.application.specifications;

import com.abc.dddtemplate.convention.Specification;
import com.abc.dddtemplate.domain.aggregates.samples.Order;
import org.springframework.stereotype.Service;

/**
 * 订单业务约束接口
 * @author <template/>
 * @date
 */
@Service
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
    public boolean valid(Order order) {
        return order.getOrderItems().size() > 0
                && order.getOrderItems().stream().mapToInt(i -> i.getPrice() * i.getNum()).sum() == order.getAmount();
    }

    @Override
    public String failMsg(Order order) {
        return "订单不符合要求";
    }
}
