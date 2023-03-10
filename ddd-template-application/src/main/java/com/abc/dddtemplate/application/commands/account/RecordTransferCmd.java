package com.abc.dddtemplate.application.commands.account;

import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.Command;
import com.abc.dddtemplate.convention.UnitOfWork;
import com.abc.dddtemplate.domain.aggregates.samples.Transfer;
import com.abc.dddtemplate.share.util.MapperUtil;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 记录转账记录
 * @author <template/>
 * @date 2023-03-10
 */
@Data
@Builder
public class RecordTransferCmd {

    /**
     * 关联账户
     */
    private Long accountId;

    /**
     * 时间
     */
    private java.util.Date time;

    /**
     * 业务类型
     */
    private Integer bizType;

    /**
     * 业务编码
     */
    private String bizId;

    /**
     * 转账金额
     */
    private Integer amount;

    @Service
    @RequiredArgsConstructor
    public static class Handler implements Command<RecordTransferCmd, Long>{
        private final AggregateRepository<Transfer, Long> transferRepository;
        private final UnitOfWork unitOfWork;
        @Override
        public Long exec(RecordTransferCmd event) {
            Transfer transfer = MapperUtil.map(event,Transfer.builder())
                    .build();
            unitOfWork.save(transfer);
            return transfer.getId();
        }
    }
}
