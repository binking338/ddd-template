package com.abc.dddtemplate.adapter.external.reports;

import com.abc.dddtemplate.adapter.domain.repositories.OrderRepository;
import com.abc.dddtemplate.application.queries.SearchOrderQry;
import com.abc.dddtemplate.share.dto.PageData;
import com.abc.dddtemplate.share.dto.PageParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author <template/>
 * @date
 */
@Service
@RequiredArgsConstructor
public class OrderReportHandlerImpl implements SearchOrderQry.OrderReport.Handler {
    private final OrderRepository orderRepository;
    @Override
    public PageData<SearchOrderQry.OrderReport> search(String owner, String key, PageParam page) {
        Page<SearchOrderQry.OrderReport> result = orderRepository.search(owner, key, page.toSpringData());
        return PageData.fromSpringData(result);
    }
}
