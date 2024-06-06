package com.abc.dddtemplate.application.queries;

import com.abc.dddtemplate.share.dto.PageData;
import com.abc.dddtemplate.share.dto.PageParam;
import com.abc.dddtemplate.convention.PageQuery;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 搜索账单
 * @author <template/>
 * @date
 */
@Data
public class SearchOrderQry extends PageParam {
    String owner;
    String key;

    @Service
    @RequiredArgsConstructor
    public static class Handler implements PageQuery<SearchOrderQry, SearchOrderQryDto> {
        private final QueryHandler queryHandler;
        @Override
        public PageData<SearchOrderQryDto> exec(SearchOrderQry searchOrderQry) {
            return queryHandler.exec(searchOrderQry);
        }
    }

    /**
     * 订单列表查询
     * 实现列表筛选、搜索场景，一般复杂的筛选场景可能会涉及多个业务源表。
     */
    @Data
    public static class SearchOrderQryDto {
        Long id;
        String name;
        Integer amount;
        Boolean finished;
        Boolean closed;
        Long billId;
    }

    public static interface QueryHandler {
        PageData<SearchOrderQryDto> exec(SearchOrderQry qry);
    }

}
