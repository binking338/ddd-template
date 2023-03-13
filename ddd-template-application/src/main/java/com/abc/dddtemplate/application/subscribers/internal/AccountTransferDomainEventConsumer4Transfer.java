package com.abc.dddtemplate.application.subscribers.internal;

import com.abc.dddtemplate.application.commands.account.RecordTransferCmd;
import com.abc.dddtemplate.domain.events.internal.AccountTransferDomainEvent;
import com.abc.dddtemplate.convention.DomainEventSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 账户转账事件
 */
@Service
@RequiredArgsConstructor
public class AccountTransferDomainEventConsumer4Transfer implements DomainEventSubscriber<AccountTransferDomainEvent> {
    private final RecordTransferCmd.Handler handler;

    @Override
    public Class<AccountTransferDomainEvent> forEventClass() {
        return AccountTransferDomainEvent.class;
    }

    @Override
    public void onEvent(AccountTransferDomainEvent event) {
        handler.exec(RecordTransferCmd.builder()
                .accountId(event.getAccountId())
                .time(new Date())
                .bizType(event.getBizType())
                .bizId(event.getBizId())
                .amount(event.getAmount())
                .build());
    }
}
