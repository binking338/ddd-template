package com.abc.dddtemplate.convention.schemas;

import com.abc.dddtemplate.convention.Schema;
import com.abc.dddtemplate.domain.aggregates.samples.Bill;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * 账单 
 * 本文件由[gen-ddd-maven-plugin]生成
 */
@RequiredArgsConstructor
public class BillSchema {
    private final Root<Bill> root;
    private final CriteriaBuilder criteriaBuilder;

    public CriteriaBuilder criteriaBuilder(){
        return criteriaBuilder;
    }

    public Schema.Field<Long> id(){
        return new Schema.Field<>(root.get("id"));
    }

    /**
     * bigint(20)
     */
    public Schema.Field<Long> orderId(){
        return new Schema.Field<>(root.get("orderId"));
    }

    /**
     * 账单名称
     * varchar(100)
     */
    public Schema.Field<String> name(){
        return new Schema.Field<>(root.get("name"));
    }

    /**
     * 支付人
     * varchar(100)
     */
    public Schema.Field<String> owner(){
        return new Schema.Field<>(root.get("owner"));
    }

    /**
     * 账单金额
     * int(11)
     */
    public Schema.Field<Integer> amount(){
        return new Schema.Field<>(root.get("amount"));
    }

    /**
     * 是否支付
     * bit(1)
     */
    public Schema.Field<Boolean> payed(){
        return new Schema.Field<>(root.get("payed"));
    }

    /**
     * 是否关闭
     * bit(1)
     */
    public Schema.Field<Boolean> closed(){
        return new Schema.Field<>(root.get("closed"));
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
    public static Specification<Bill> specify(Schema.PredicateBuilder<BillSchema> builder){
        return (root, criteriaQuery, criteriaBuilder) -> {
            BillSchema bill = new BillSchema(root, criteriaBuilder);
            criteriaQuery.where(builder.build(bill));
            return null;
        };
    }

}
