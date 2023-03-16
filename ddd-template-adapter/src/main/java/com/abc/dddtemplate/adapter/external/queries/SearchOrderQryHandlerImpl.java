package com.abc.dddtemplate.adapter.external.queries;

import com.abc.dddtemplate.adapter.domain.repositories.OrderRepository;
import com.abc.dddtemplate.application.queries.SearchOrderQry;
import com.abc.dddtemplate.share.dto.PageData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author <template/>
 * @date
 */
@Service
@RequiredArgsConstructor
public class SearchOrderQryHandlerImpl implements SearchOrderQry.Handler {
    private final OrderRepository orderRepository;

    @Override
    public PageData<SearchOrderQry.OrderReport> exec(SearchOrderQry searchOrderQry) {
        Page<SearchOrderQry.OrderReport> result = orderRepository.search(searchOrderQry.getOwner(), searchOrderQry.getKey(), searchOrderQry.toSpringData());
        return PageData.fromSpringData(result);
    }
}
