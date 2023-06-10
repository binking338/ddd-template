package com.abc.dddtemplate.adapter.application.queries;

import com.abc.dddtemplate.application.queries.SearchOrderQry;
import com.abc.dddtemplate.domain.aggregates.samples.Order;
import com.abc.dddtemplate.share.dto.PageData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

/**
 * @author <template/>
 * @date
 */
public interface SearchOrderQryImpl extends Repository<Order, Long> {

    String SEARCH_SQL = "from `order` o  \n" +
            "join bill b on b.order_id=o.id and b.db_deleted=false \n" +
            "where o.db_deleted=false and o.owner = :o and o.name like concat('%', :k,'%') ";
    @Query(nativeQuery = true,
            countQuery = "select count(*)\n" + SEARCH_SQL,
            value = "select o.id,o.name,o.amount,o.closed,o.finished,b.id billId \n" + SEARCH_SQL)
    Page<SearchOrderQry.OrderReport> search(@Param("o") String owner, @Param("k") String key, Pageable pageable);

    @Service
    @RequiredArgsConstructor
    public static class SearchOrderQryHandlerAdapter implements SearchOrderQry.Handler {
        private final SearchOrderQryImpl impl;

        @Override
        public PageData<SearchOrderQry.OrderReport> exec(SearchOrderQry searchOrderQry) {
            Page<SearchOrderQry.OrderReport> result = impl.search(searchOrderQry.getOwner(), searchOrderQry.getKey(), searchOrderQry.toSpringData());
            return PageData.fromSpringData(result);
        }
    }
}
