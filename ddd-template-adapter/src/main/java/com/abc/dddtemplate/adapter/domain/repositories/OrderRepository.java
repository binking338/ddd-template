package com.abc.dddtemplate.adapter.domain.repositories;

import com.abc.dddtemplate.application.queries.SearchOrderQry;
import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.domain.aggregates.samples.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 本文件由[gen-ddd-maven-plugin]生成，请不要手工改动
 */
public interface OrderRepository extends AggregateRepository<Order, Long> {
    // 【自定义代码开始】本段落之外代码由[gen-ddd-maven-plugin]维护，请不要手工改动

    String SEARCH_SQL = "from `order` o  \n" +
            "join bill b on b.order_id=o.id and b.db_deleted=false \n" +
            "where o.db_deleted=false and o.accountName = :o and o.name like concat('%', :k,'%') ";
    @Query(nativeQuery = true,
        countQuery = "select count(*)\n" + SEARCH_SQL,
        value = "select o.id,o.name,o.amount,o.closed,o.finished,b.id billId \n" + SEARCH_SQL)
    Page<SearchOrderQry.OrderReport> search(@Param("o") String owner, @Param("k") String key, Pageable pageable);


    // 【自定义代码结束】本段落之外代码由[gen-ddd-maven-plugin]维护，请不要手工改动
}
