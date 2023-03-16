package com.abc.dddtemplate.application.commands.account;

import com.abc.dddtemplate.convention.Command;
import com.abc.dddtemplate.domain.aggregates.samples.Account;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.UnitOfWork;
import com.abc.dddtemplate.share.exception.KnownException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 账户余额充值
 *
 * @author <template/>
 * @date
 */
@Data
public class ChargeAccountCmd {
    /**
     * 账户Id
     */
    Long accountId;
    /**
     * 账户名称
     */
    String accountName;
    /**
     * 转账编号
     */
    String transferNo;
    /**
     * 金额
     */
    Integer amount;

    @Service
    public static class Handler implements Command<ChargeAccountCmd, Boolean> {
        @Autowired
        AggregateRepository<Account, Long> accountAggregateRepository;
        @Autowired
        UnitOfWork unitOfWork;

        @Override
        public Boolean exec(ChargeAccountCmd chargeAccountCmd) {
            Account account = accountAggregateRepository.findById(chargeAccountCmd.accountId)
                    .orElseThrow(()-> new KnownException("账户不存在"));
            if (!account.getName().equals(chargeAccountCmd.getAccountName())) {
                return false;
            }
            account.charge(chargeAccountCmd.transferNo, chargeAccountCmd.amount);
            unitOfWork.save(account);
            return true;
        }
    }
}
