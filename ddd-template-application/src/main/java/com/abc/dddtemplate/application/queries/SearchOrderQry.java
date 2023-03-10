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
    public static class Handler implements PageQuery<SearchOrderQry, OrderReport> {
        private final OrderReport.Handler orderReportHandler;

        @Override
        public PageData<OrderReport> exec(SearchOrderQry searchOrderParam) {
            return orderReportHandler.search(searchOrderParam.getOwner(), searchOrderParam.getKey(), searchOrderParam);
        }
    }

    /**
     * 订单列表查询
     * 实现列表筛选、搜索场景，一般复杂的筛选场景可能会涉及多个业务源表。
     * @author <template/>
     * @date
     */
    public interface OrderReport {
        Long getId();
        String getName();
        Integer getAmount();
        Boolean getFinished();
        Boolean getClosed();
        Long getBillId();

        interface Handler
        {
            PageData<OrderReport> search(String owner, String key, PageParam page);
        }
    }

}
