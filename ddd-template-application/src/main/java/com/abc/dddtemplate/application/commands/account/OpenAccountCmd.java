package com.abc.dddtemplate.application.commands.account;

import com.abc.dddtemplate.convention.Command;
import com.abc.dddtemplate.domain.aggregates.samples.Account;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.UnitOfWork;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 开户
 * @author <template/>
 * @date
 */
@Data
public class OpenAccountCmd {
    private String name;

    @Service
    @RequiredArgsConstructor
    public static class Handler implements Command<OpenAccountCmd, Long> {
        private final AggregateRepository<Account, Long> accountRepository;
        private final UnitOfWork unitOfWork;

        @Override
        public Long exec(OpenAccountCmd chargeAccountCmd) {
            Account account = Account.open(chargeAccountCmd.name);
            unitOfWork.required(() -> {
                accountRepository.saveAndFlush(account);
            });
            return account.getId();
        }
    }
}
