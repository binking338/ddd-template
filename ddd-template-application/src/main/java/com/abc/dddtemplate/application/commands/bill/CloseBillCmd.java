package com.abc.dddtemplate.application.commands.bill;

import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.Command;
import com.abc.dddtemplate.convention.UnitOfWork;
import com.abc.dddtemplate.convention.schemas.BillSchema;
import com.abc.dddtemplate.domain.aggregates.samples.Bill;
import com.abc.dddtemplate.share.exception.ErrorException;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 关闭账单
 * @author <template/>
 * @date 2023-03-10
 */
@Data
@Builder
public class CloseBillCmd {
    /**
     * 订单Id
     */
    private Long orderId;

    @Service
    @RequiredArgsConstructor
    public static class Handler implements Command<CloseBillCmd, Boolean>{
        private final AggregateRepository<Bill, Long> billRepository;
        private final UnitOfWork unitOfWork;

        @Override
        public Boolean exec(CloseBillCmd cmd) {
            Bill bill = billRepository.findOne(BillSchema.specify(b -> b.orderId().eq(cmd.getOrderId())))
                    .orElseThrow(() -> new ErrorException("账单丢失"));
            bill.close();
            unitOfWork.save(bill);
//            unitOfWork.required(() -> {
//                billRepository.save(bill);
//                // throw new WarnException("测试UnitOfWork的静态方法实现");
//                return null;
//            });
            return true;
        }
    }
}
