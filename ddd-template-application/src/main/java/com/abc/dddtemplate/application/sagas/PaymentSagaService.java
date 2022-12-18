package com.abc.dddtemplate.application.sagas;

import com.abc.dddtemplate.convention.schemas.AccountSchema;
import com.abc.dddtemplate.convention.schemas.BillSchema;
import com.abc.dddtemplate.domain.aggregates.samples.Account;
import com.abc.dddtemplate.domain.aggregates.samples.Bill;
import com.abc.dddtemplate.domain.aggregates.samples.Transfer;
import com.abc.dddtemplate.external.clients.CouponClient;
import com.abc.dddtemplate.share.exception.ErrorException;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.SagaStateMachine;
import com.abc.dddtemplate.convention.UnitOfWork;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 账单支付，包含优惠券扣减
 *
 * @author <template/>
 * @date
 */
@Service
@Slf4j
public class PaymentSagaService extends SagaStateMachine<PaymentSagaService.Context> {

    @Autowired
    CouponClient couponClient;
    @Autowired
    AggregateRepository<Account, Long> accountRepository;
    @Autowired
    AggregateRepository<Bill, Long> billRepository;

    @Override
    protected Integer getBizType() {
        return 1000;
    }

    @Override
    protected Class<Context> getContextClass() {
        return PaymentSagaService.Context.class;
    }

    @Override
    protected Process<Context> config() {
        return Process.of((Context context) -> {
            // 扣减优惠券
            couponClient.deduct(context.getUser(), context.getCouponAmount());
        }).then(context -> {
            // 扣减账户余额
            Optional<Account> accountOptional = accountRepository
                    .findOne(AccountSchema.specify(root -> root.name().eq(context.user)));
            if (!accountOptional.isPresent()) {
                throw new ErrorException(context.user + " 的账户不存在");
            }
            accountOptional.get().use(Transfer.BILL, context.billId, context.accountAmount);
            UnitOfWork.saveEntities(accountOptional.get());
        }).then(context -> {
            // 更新账单状态
            Optional<Bill> billOptional = billRepository.findOne(BillSchema.specify(root -> root.id().eq(context.billId)));
            if (!billOptional.isPresent()) {
                throw new ErrorException(context.user + context.billId + " 的账单不存在");
            }
            billOptional.get().pay(context.couponAmount + context.accountAmount);
            UnitOfWork.saveEntities(billOptional.get());
        }).root();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Context {
        String user;
        Long billId;
        Integer couponAmount;
        Integer accountAmount;
    }
}
