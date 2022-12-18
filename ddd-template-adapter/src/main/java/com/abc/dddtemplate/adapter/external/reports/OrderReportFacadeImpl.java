package com.abc.dddtemplate.adapter.external.reports;

import com.abc.dddtemplate.adapter.domain.repositories.OrderRepository;
import com.abc.dddtemplate.external.reports.OrderReport;
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
public class OrderReportFacadeImpl implements OrderReport.Facade {
    private final OrderRepository orderRepository;
    @Override
    public PageData<OrderReport> search(String owner, String key, PageParam page) {
        Page<OrderReport> result = orderRepository.search(owner, key, page.toSpringData());
        return PageData.fromSpringData(result);
    }
}
