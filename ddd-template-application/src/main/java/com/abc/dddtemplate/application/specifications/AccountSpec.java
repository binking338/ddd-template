package com.abc.dddtemplate.application.specifications;

import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.Specification;
import com.abc.dddtemplate.convention.schemas.AccountSchema;
import com.abc.dddtemplate.domain.aggregates.samples.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 账户业务约束接口
 * @author <template/>
 * @date 2023-03-13
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountSpec  implements Specification<Account> {

    private final AggregateRepository<Account, Long> accountRepo;

    @Override
    public Class<Account> entityClass() {
        return Account.class;
    }

    @Override
    public boolean valid(Account account) {
        if(account.getId() == null) {
            boolean sameName = accountRepo.count(AccountSchema.specify(builder -> builder.name().eq(account.getName()))) > 0;
            return !sameName;
        } else {
            return true;
        }
    }

    @Override
    public String failMsg(Account account) {
        return "存在同名账户";
    }
}
