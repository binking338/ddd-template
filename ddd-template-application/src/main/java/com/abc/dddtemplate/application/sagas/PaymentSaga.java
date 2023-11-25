package com.abc.dddtemplate.application.sagas;

import com.abc.dddtemplate.application.commands.bill.PayBillCmd;
import com.abc.dddtemplate.application.clients.CouponClient;
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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSaga {

    /**
     * 用户
     */
    String accountName;
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

    @Service
    @Slf4j
    public static class Handler extends SagaStateMachine<PaymentSaga> {
        @Autowired
        CouponClient couponClient;
        @Autowired
        PayBillCmd.Handler payBillCmdHandler;

        @Override
        protected Integer getBizType() {
            return 1000;
        }

        @Override
        protected Class<PaymentSaga> getContextClass() {
            return PaymentSaga.class;
        }

        @Override
        protected Process<PaymentSaga> config() {
            return Process.of((PaymentSaga context) -> {
                // 扣减优惠券
                if (context.couponAmount == null || context.couponAmount == 0) {
                    return;
                }
                couponClient.deduct(context.accountName, context.getCouponAmount());
            }).then(context -> {
                // 支付
                payBillCmdHandler.exec(PayBillCmd.builder()
                        .owner(context.accountName)
                        .billId(context.getBillId())
                        .amount(context.getAccountAmount())
                        .build());
            }).root();
        }
    }
}
