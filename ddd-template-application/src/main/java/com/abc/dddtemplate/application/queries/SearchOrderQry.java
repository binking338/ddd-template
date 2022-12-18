package com.abc.dddtemplate.application.queries;

import com.abc.dddtemplate.share.dto.PageData;
import com.abc.dddtemplate.share.dto.PageParam;
import com.abc.dddtemplate.convention.PageQuery;
import com.abc.dddtemplate.external.reports.OrderReport;
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
        private final OrderReport.Facade orderReportFacade;

        @Override
        public PageData<OrderReport> exec(SearchOrderQry searchOrderParam) {
            return orderReportFacade.search(searchOrderParam.getOwner(), searchOrderParam.getKey(), searchOrderParam);
        }
    }
}
