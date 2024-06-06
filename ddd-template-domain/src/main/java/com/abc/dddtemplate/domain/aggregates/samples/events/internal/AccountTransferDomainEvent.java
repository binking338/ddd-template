package com.abc.dddtemplate.domain.aggregates.samples.events.internal;

import com.abc.dddtemplate.share.annotation.DomainEvent;
import lombok.*;

/**
 * 领域事件 - 账户金额转移
 * @author <template/>
 * @date
 */
@DomainEvent
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransferDomainEvent {
    Long accountId;
    Integer bizType;
    String bizId;
    Integer amount;
}
