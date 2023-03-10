package com.abc.dddtemplate.application.sagas;

import com.abc.dddtemplate.application.commands.bill.PayBillCmd;
import com.abc.dddtemplate.external.clients.CouponClient;
import com.abc.dddtemplate.convention.SagaStateMachine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 账单支付，包含优惠券扣减
 *
 * @author <template/>
 * @date
 */
@Service
@Slf4j
public class PaymentSagaService extends SagaStateMachine<PaymentSagaService.PaymentSagaContext> {

    @Autowired
    CouponClient couponClient;
    @Autowired
    PayBillCmd.Handler payBillCmdHandler;

    @Override
    protected Integer getBizType() {
        return 1000;
    }

    @Override
    protected Class<PaymentSagaContext> getContextClass() {
        return PaymentSagaService.PaymentSagaContext.class;
    }

    @Override
    protected Process<PaymentSagaContext> config() {
        return Process.of((PaymentSagaContext context) -> {
            // 扣减优惠券
            if (context.couponAmount == null || context.couponAmount == 0) {
                return;
            }
            couponClient.deduct(context.getUser(), context.getCouponAmount());
        }).then(context -> {
            // 支付
            payBillCmdHandler.exec(PayBillCmd.builder()
                    .accountName(context.user)
                    .billId(context.getBillId())
                    .amount(context.getAccountAmount())
                    .build());
        }).root();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentSagaContext {
        /**
         * 用户
         */
        String user;
        /**
         * 账单
         */
        Long billId;
        /**
         * 优惠券抵扣金额
         */
        Integer couponAmount;
        /**
         * 账户花费金额
         */
        Integer accountAmount;
    }
}
