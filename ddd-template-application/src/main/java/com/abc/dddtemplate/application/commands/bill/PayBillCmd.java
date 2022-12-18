package com.abc.dddtemplate.application.commands.bill;

import com.abc.dddtemplate.application.sagas.PaymentSagaService;
import com.abc.dddtemplate.convention.schemas.AccountSchema;
import com.abc.dddtemplate.domain.aggregates.samples.Account;
import com.abc.dddtemplate.domain.aggregates.samples.Bill;
import com.abc.dddtemplate.domain.services.PaymentDomainService;
import com.abc.dddtemplate.share.exception.ErrorException;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.Command;
import com.abc.dddtemplate.convention.UnitOfWork;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 命令模式 （风格一）
 * 支付账单
 *
 * @author <template/>
 * @date
 */
@Data
public class PayBillCmd {
    String owner;
    Long billId;
    Integer accountAmount;
    Integer couponAmount;

    @Service
    @RequiredArgsConstructor
    public static class Handler implements Command<PayBillCmd, Boolean> {
        private final PaymentDomainService paymentDomainService;
        private final AggregateRepository<Account, Long> accountRepository;
        private final AggregateRepository<Bill, Long> billRepository;
        private final PaymentSagaService paymentSagaService;

        @Override
        public Boolean exec(PayBillCmd payBillDTO) {
            if (payBillDTO.couponAmount == null || payBillDTO.couponAmount == 0) {
                Bill bill = billRepository.findById(payBillDTO.getBillId()).orElse(null);
                if (bill == null) {
                    return false;
                }
                Optional<Account> accountOptional = accountRepository
                        .findOne(AccountSchema.specify(root -> root.name().eq(payBillDTO.owner)));
                if (!accountOptional.isPresent()) {
                    throw new ErrorException(payBillDTO.owner + " 的账户不存在");
                }
                paymentDomainService.pay(accountOptional.get(), bill);
                UnitOfWork.saveEntities(accountOptional.get(), bill);
                return true;
            } else {
                paymentSagaService.run(PaymentSagaService.Context.builder()
                        .user(payBillDTO.owner)
                        .billId(payBillDTO.billId)
                        .couponAmount(payBillDTO.couponAmount)
                        .accountAmount(payBillDTO.accountAmount)
                        .build());
                return true;
            }
        }
    }
}
