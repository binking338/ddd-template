package com.abc.dddtemplate.adapter.application.queries;

import com.abc.dddtemplate.adapter.infra.mapper.OrderMapper;
import com.abc.dddtemplate.application.queries.SearchOrderQry;
import com.abc.dddtemplate.share.dto.PageData;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author <template/>
 * @date
 */
@Service
@RequiredArgsConstructor
public class SearchOrderQryQueryHandlerImpl implements SearchOrderQry.QueryHandler {
    private final OrderMapper orderMapper;
    @Override
    public PageData<SearchOrderQry.SearchOrderQryDto> exec(SearchOrderQry qry) {
        PageInfo<SearchOrderQry.SearchOrderQryDto> page = PageHelper.startPage(qry.getPageNum(), qry.getPageSize())
                .doSelectPageInfo(() -> orderMapper.search(qry.getOwner(), qry.getKey()));
        return PageData.create(qry, page.getTotal(), page.getList());
    }
}
