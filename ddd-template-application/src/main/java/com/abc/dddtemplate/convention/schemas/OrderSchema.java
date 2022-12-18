package com.abc.dddtemplate.convention.schemas;

import com.abc.dddtemplate.convention.Schema;
import com.abc.dddtemplate.domain.aggregates.samples.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * 订单 
 * 本文件由[gen-ddd-maven-plugin]生成
 */
@RequiredArgsConstructor
public class OrderSchema {
    private final Root<Order> root;
    private final CriteriaBuilder criteriaBuilder;

    public CriteriaBuilder criteriaBuilder(){
        return criteriaBuilder;
    }

    public Schema.Field<Long> id(){
        return new Schema.Field<>(root.get("id"));
    }

    /**
     * 订单金额
     * int(11)
     */
    public Schema.Field<Integer> amount(){
        return new Schema.Field<>(root.get("amount"));
    }

    /**
     * 订单标题
     * varchar(100)
     */
    public Schema.Field<String> name(){
        return new Schema.Field<>(root.get("name"));
    }

    /**
     * 下单人
     * varchar(100)
     */
    public Schema.Field<String> owner(){
        return new Schema.Field<>(root.get("owner"));
    }

    /**
     * 是否完成
     * bit(1)
     */
    public Schema.Field<Boolean> finished(){
        return new Schema.Field<>(root.get("finished"));
    }

    /**
     * 是否关闭
     * bit(1)
     */
    public Schema.Field<Boolean> closed(){
        return new Schema.Field<>(root.get("closed"));
    }

    /**
     * datetime
     */
    public Schema.Field<java.util.Date> updateAt(){
        return new Schema.Field<>(root.get("updateAt"));
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
    public static Specification<Order> specify(Schema.PredicateBuilder<OrderSchema> builder){
        return (root, criteriaQuery, criteriaBuilder) -> {
            OrderSchema order = new OrderSchema(root, criteriaBuilder);
            criteriaQuery.where(builder.build(order));
            return null;
        };
    }

}
