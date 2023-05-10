package com.abc.dddtemplate.domain.events.external;

import com.abc.dddtemplate.domain.aggregates.samples.Bill;
import com.abc.dddtemplate.share.Constants;
import com.abc.dddtemplate.share.annotation.DomainEvent;
import lombok.*;

/**
 * 集成事件 - 账单已支付
 * @author <template/>
 * @date
 */
@DomainEvent("domainevent-ddd-template-billpaid")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillPaidDomainEvent {

    private Bill bill;
}
