package com.abc.dddtemplate.external.reports;

import com.abc.dddtemplate.share.dto.PageData;
import com.abc.dddtemplate.share.dto.PageParam;

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

    interface Facade
    {
        PageData<OrderReport> search(String owner, String key, PageParam page);
    }
}
