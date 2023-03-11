package com.abc.dddtemplate.application.commands.bill;

import com.abc.dddtemplate.convention.schemas.AccountSchema;
import com.abc.dddtemplate.domain.aggregates.samples.Account;
import com.abc.dddtemplate.domain.aggregates.samples.Bill;
import com.abc.dddtemplate.domain.services.PaymentDomainService;
import com.abc.dddtemplate.share.exception.ErrorException;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.Command;
import lombok.Builder;
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
@Builder
public class PayBillCmd {
    String accountName;
    Long billId;
    Integer amount;

    @Service
    @RequiredArgsConstructor
    public static class Handler implements Command<PayBillCmd, Boolean> {
        private final PaymentDomainService paymentDomainService;
        private final AggregateRepository<Account, Long> accountRepository;
        private final AggregateRepository<Bill, Long> billRepository;

        @Override
        public Boolean exec(PayBillCmd cmd) {
            Bill bill = billRepository.findById(cmd.getBillId()).orElse(null);
            if (bill == null) {
                throw new ErrorException(cmd.getBillId() + " 账单不存在");
            }
            Optional<Account> accountOptional = accountRepository
                    .findOne(AccountSchema.specify(root -> root.name().eq(cmd.accountName)));
            if (!accountOptional.isPresent()) {
                throw new ErrorException(cmd.accountName + " 的账户不存在");
            }
            paymentDomainService.pay(accountOptional.get(), bill);
            return true;
        }
    }
}
