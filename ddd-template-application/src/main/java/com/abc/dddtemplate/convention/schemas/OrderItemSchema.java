package com.abc.dddtemplate.convention.schemas;

import com.abc.dddtemplate.convention.Schema;
import com.abc.dddtemplate.domain.aggregates.samples.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * 订单项 
 * 本文件由[gen-ddd-maven-plugin]生成
 */
@RequiredArgsConstructor
public class OrderItemSchema {
    private final Root<OrderItem> root;
    private final CriteriaBuilder criteriaBuilder;

    public CriteriaBuilder criteriaBuilder(){
        return criteriaBuilder;
    }

    public Schema.Field<Long> id(){
        return new Schema.Field<>(root.get("id"));
    }

    /**
     * 订单项名称
     * varchar(100)
     */
    public Schema.Field<String> name(){
        return new Schema.Field<>(root.get("name"));
    }

    /**
     * 单价
     * int(11)
     */
    public Schema.Field<Integer> price(){
        return new Schema.Field<>(root.get("price"));
    }

    /**
     * 数量
     * int(11)
     */
    public Schema.Field<Integer> num(){
        return new Schema.Field<>(root.get("num"));
    }

    /**
     * 满足所有条件
     * @param restrictions
     * @return
     */
    public Predicate all(Predicate... restrictions){
        return criteriaBuilder().and(restrictions);
    }

    /**
     * 满足任一条件
     * @param restrictions
     * @return
     */
    public Predicate any(Predicate... restrictions){
        return criteriaBuilder().or(restrictions);
    }

    /**
     * 构建查询条件
     * @param builder
     * @return
     */
    public static Specification<OrderItem> specify(Schema.PredicateBuilder<OrderItemSchema> builder){
        return (root, criteriaQuery, criteriaBuilder) -> {
            OrderItemSchema orderItem = new OrderItemSchema(root, criteriaBuilder);
            criteriaQuery.where(builder.build(orderItem));
            return null;
        };
    }

}
