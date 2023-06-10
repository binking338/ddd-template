package com.abc.dddtemplate.application.queries;

import com.abc.dddtemplate.convention.schemas.TransferSchema;
import com.abc.dddtemplate.domain.aggregates.samples.Transfer;
import com.abc.dddtemplate.share.dto.PageData;
import com.abc.dddtemplate.share.dto.PageParam;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.PageQuery;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.Date;

/**
 * 转账列表查询
 * @author <template/>
 * @date
 */
@Data
public class ListTransferQry extends PageParam {
    Long accountId;
    Date begin;
    Date end;

    @Service
    @RequiredArgsConstructor
    public static class Handler implements PageQuery<ListTransferQry, ListTransferQryDto> {
        private final AggregateRepository<Transfer, Long> transferRepository;

        @Override
        public PageData<ListTransferQryDto> exec(ListTransferQry listTransferQry) {
            Page<Transfer> page = transferRepository.findAll(TransferSchema.specify(transfer -> {
                Predicate predicate = null;
                predicate = transfer.accountId().equal(listTransferQry.accountId);
                if (listTransferQry.begin != null) {
                    predicate = transfer.criteriaBuilder().and(predicate, transfer.time().greaterThan(listTransferQry.begin));
                }
                if (listTransferQry.end != null) {
                    predicate = transfer.criteriaBuilder().and(predicate, transfer.time().greaterThan(listTransferQry.end));
                }
                return predicate;
            }), listTransferQry.toSpringData());

            return PageData.fromSpringData(page, ListTransferQryDto.class);
        }
    }

    @Data
    public static class ListTransferQryDto {

        private Long id;
        private Long accountId;

        /**
         * 转账时间
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
    }
}
