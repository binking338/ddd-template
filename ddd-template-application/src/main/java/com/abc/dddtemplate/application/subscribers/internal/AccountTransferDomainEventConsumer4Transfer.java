package com.abc.dddtemplate.application.subscribers.internal;

import com.abc.dddtemplate.domain.aggregates.samples.Transfer;
import com.abc.dddtemplate.domain.events.internal.AccountTransferDomainEvent;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.DomainEventSubscriber;
import com.abc.dddtemplate.convention.UnitOfWork;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 账户转账事件
 */
@Service
@RequiredArgsConstructor
public class AccountTransferDomainEventConsumer4Transfer implements DomainEventSubscriber<AccountTransferDomainEvent> {
    private final AggregateRepository<Transfer, Long> transferRepository;
    private final UnitOfWork unitOfWork;

    @Override
    public Class<AccountTransferDomainEvent> forEventClass() {
        return AccountTransferDomainEvent.class;
    }

    @Override
    public void onEvent(AccountTransferDomainEvent event) {
        Transfer transfer = Transfer.builder()
                .accountId(event.getAccountId())
                .time(new Date())
                .bizType(event.getBizType())
                .bizId(event.getBizId())
                .amount(event.getAmount())
                .build();
        unitOfWork.required(() -> {
            transferRepository.save(transfer);
        });
    }
}
