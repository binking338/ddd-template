package com.abc.dddtemplate.domain.services;

import com.abc.dddtemplate.domain.aggregates.samples.Account;
import com.abc.dddtemplate.domain.aggregates.samples.Bill;
import com.abc.dddtemplate.domain.aggregates.samples.Transfer;
import org.springframework.stereotype.Service;

/**
 * 账户领域服务
 * <p>
 * 1.逻辑实现不得依赖聚合仓储以及其他服务RPC。
 * 2.慎用领域服务！！！滥用易导致贫血模型，如果确实逻辑有较高复用性，可参考 Rule of Three 原则。
 *
 * @author <template/>
 * @date
 */
@Service
public class PaymentDomainService {

    /**
     * 支付账单
     *
     * @param account
     * @param bill
     */
    public void pay(Account account, Bill bill) {
        account.deduct(Transfer.BILL, bill.getId(), bill.getAmount());
        bill.pay(bill.getAmount());
    }

}
