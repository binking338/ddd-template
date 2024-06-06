package com.abc.dddtemplate.application.commands.account;

import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.Command;
import com.abc.dddtemplate.convention.UnitOfWork;
import com.abc.dddtemplate.convention.schemas.AccountSchema;
import com.abc.dddtemplate.domain.aggregates.samples.Account;
import com.abc.dddtemplate.domain.aggregates.samples.Transfer;
import com.abc.dddtemplate.share.exception.ErrorException;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 花费账户
 * @author <template/>
 * @date 2023-03-11
 */
@Data
@Builder
public class DeductAccountCmd {
    String accountName;
    Long billId;
    Integer amount;

    @Service
    @RequiredArgsConstructor
    public static class Handler implements Command<DeductAccountCmd, Boolean>{
        private final AggregateRepository<Account, Long> accountRepository;

        @Override
        public Boolean exec(DeductAccountCmd cmd) {
            Account account = accountRepository
                    .findOne(AccountSchema.specify(root -> root.name().eq(cmd.accountName)))
                    .orElseThrow(()->new ErrorException(cmd.accountName + " 的账户不存在"));

            account.deduct(Transfer.BILL, cmd.billId, cmd.amount);
            UnitOfWork.saveEntities(account);
            return true;
        }
    }
}
