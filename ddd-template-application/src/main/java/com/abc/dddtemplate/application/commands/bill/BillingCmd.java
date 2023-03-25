package com.abc.dddtemplate.application.commands.bill;

import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.Command;
import com.abc.dddtemplate.convention.UnitOfWork;
import com.abc.dddtemplate.domain.aggregates.samples.Bill;
import com.abc.dddtemplate.share.util.MapperUtil;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 下账单
 * @author <template/>
 * @date 2023-03-10
 */
@Data
@Builder
public class BillingCmd {

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 账单名称
     */
    private String name;

    /**
     * 支付人
     */
    private String owner;

    /**
     * 账单金额
     */
    private Integer amount;

    @Service
    @RequiredArgsConstructor
    public static class  Handler implements Command<BillingCmd, Long>{
//        private final AggregateRepository<Bill, Long> billRepository;
        private final UnitOfWork unitOfWork;

        @Override
        public Long exec(BillingCmd billingCmd) {
            Bill bill = MapperUtil.map(billingCmd, Bill.builder()).build();
            unitOfWork.save(bill);
//            unitOfWork.required(() -> {
//                billRepository.save(bill);
//                // throw new WarnException("测试UnitOfWork的实例方法实现");
//                return null;
//            });
            return bill.getId();
        }
    }
}
