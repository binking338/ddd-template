package com.abc.dddtemplate.application.queries;

import com.abc.dddtemplate.convention.schemas.BillSchema;
import com.abc.dddtemplate.domain.aggregates.samples.Bill;
import com.abc.dddtemplate.share.dto.PageData;
import com.abc.dddtemplate.share.dto.PageParam;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.PageQuery;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Expression;

/**
 * 账单列表查询
 * @author <template/>
 * @date
 */
@Data
public class ListBillQry extends PageParam {
    String owner;
    String name;

    @Service
    @RequiredArgsConstructor
    public static class Handler implements PageQuery<ListBillQry, ListBillQryDto> {
        private final AggregateRepository<Bill, Long> billRepository;

        @Override
        public PageData<ListBillQryDto> exec(ListBillQry listBillQry) {
            Page<Bill> page = billRepository.findAll(BillSchema.specify(bill -> {
                Expression<Boolean> predicate = null;
                predicate = bill.owner().equal(listBillQry.owner);
                if (listBillQry.name != null) {
                    predicate = bill.name().like("%" + listBillQry.name + "%");
                }
                return predicate;
            }), listBillQry.toSpringData());
            return PageData.fromSpringData(page, ListBillQryDto.class);
        }
    }

    @Data
    public static class ListBillQryDto {

        private Long id;

        /**
         *
         */
        private Long orderId;

        /**
         * 账单名称
         */
        private String name;

        /**
         * 账单金额
         */
        private Integer amount;

        /**
         * 所有人
         */
        private String owner;

        /**
         * 是否支付
         */
        private Boolean payed;

        /**
         * 是否关闭
         */
        private Boolean closed;
    }
}
