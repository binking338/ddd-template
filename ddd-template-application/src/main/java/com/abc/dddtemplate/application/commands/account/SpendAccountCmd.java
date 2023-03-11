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

import java.util.Optional;

/**
 * 花费账户
 * @author <template/>
 * @date 2023-03-11
 */
@Data
@Builder
public class SpendAccountCmd {
    String accountName;
    Long billId;
    Integer amount;

    @Service
    @RequiredArgsConstructor
    public static class Handler implements Command<SpendAccountCmd, Boolean>{
        private final AggregateRepository<Account, Long> accountRepository;

        @Override
        public Boolean exec(SpendAccountCmd cmd) {

            Optional<Account> accountOptional = accountRepository
                    .findOne(AccountSchema.specify(root -> root.name().eq(cmd.accountName)));
            if (!accountOptional.isPresent()) {
                throw new ErrorException(cmd.accountName + " 的账户不存在");
            }
            accountOptional.get().spend(Transfer.BILL, cmd.billId, cmd.amount);
            UnitOfWork.saveEntities(accountOptional.get());
            return true;
        }
    }
}
