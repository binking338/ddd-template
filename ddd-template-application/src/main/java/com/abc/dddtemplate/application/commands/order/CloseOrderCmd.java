package com.abc.dddtemplate.application.commands.order;

import com.abc.dddtemplate.convention.Command;
import com.abc.dddtemplate.domain.aggregates.samples.Order;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.UnitOfWork;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 命令模式 （风格二）
 * 关闭订单
 *
 * @author <template/>
 * @date
 */
@Service
@RequiredArgsConstructor
public class CloseOrderCmd implements Command<Long, Boolean> {
    private final AggregateRepository<Order, Long> orderRepository;
    private final UnitOfWork unitOfWork;

    @Override
    public Boolean exec(Long id) {
        Order order =  unitOfWork.required(() ->  orderRepository.findById(id).orElse(null));
        if (order == null) {
            return false;
        }
        order.close();
        unitOfWork.save(order);
        return true;
    }
}
