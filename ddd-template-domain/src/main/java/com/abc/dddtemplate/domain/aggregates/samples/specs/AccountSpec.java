package com.abc.dddtemplate.domain.aggregates.samples.specs;

import com.abc.dddtemplate.convention.Specification;
import com.abc.dddtemplate.domain.aggregates.samples.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 账户业务约束接口
 *
 * @author <template/>
 * @date 2023-03-13
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AccountSpec implements Specification<Account> {

    @Override
    public Class<Account> entityClass() {
        return Account.class;
    }

    @Override
    public Result valid(Account account) {
        if (account.getId() == null && StringUtils.isBlank(account.getName())) {
            return Result.fail("账户名缺失");
        }
        return Result.pass();
    }
}
