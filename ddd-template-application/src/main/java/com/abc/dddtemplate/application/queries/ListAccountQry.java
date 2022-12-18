package com.abc.dddtemplate.application.queries;

import com.abc.dddtemplate.share.dto.PageData;
import com.abc.dddtemplate.share.dto.PageParam;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.PageQuery;
import com.abc.dddtemplate.convention.schemas.AccountSchema;
import com.abc.dddtemplate.domain.aggregates.samples.Account;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * 账户列表查询
 * @author <template/>
 * @date
 */
@Data
public class ListAccountQry extends PageParam {
    String name;

    @Service
    @RequiredArgsConstructor
    public static class Handler implements PageQuery<ListAccountQry, ListAccountQryDto> {
        private final AggregateRepository<Account, Long> accountRepository;

        @Override
        public PageData<ListAccountQryDto> exec(ListAccountQry listAccountQry) {
            Page<Account> page = listAccountQry.name != null
                    ? accountRepository.findAll(AccountSchema.specify(root -> root.name().like("%" + listAccountQry.name + "%")), listAccountQry.toSpringData())
                    : accountRepository.findAll(listAccountQry.toSpringData());
            return PageData.fromSpringData(page, ListAccountQryDto.class);
        }
    }

    @Data
    public static class ListAccountQryDto {
        private Long id;

        /**
         * 账户名称
         */
        @Schema(description = "账户名称")
        private String name;

        /**
         * 账户金额
         */
        @Schema(description = "账户金额")
        private Integer amount;
    }
}
