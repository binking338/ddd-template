package com.abc.dddtemplate.convention.schemas;

import com.abc.dddtemplate.convention.Schema;
import com.abc.dddtemplate.domain.aggregates.samples.Transfer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * 转账记录 
 * 本文件由[gen-ddd-maven-plugin]生成
 */
@RequiredArgsConstructor
public class TransferSchema {
    private final Root<Transfer> root;
    private final CriteriaBuilder criteriaBuilder;

    public CriteriaBuilder criteriaBuilder(){
        return criteriaBuilder;
    }

    public Schema.Field<Long> id(){
        return new Schema.Field<>(root.get("id"));
    }

    /**
     * 关联账户
     * bigint(100)
     */
    public Schema.Field<Long> accountId(){
        return new Schema.Field<>(root.get("accountId"));
    }

    /**
     * 时间
     * datetime
     */
    public Schema.Field<java.util.Date> time(){
        return new Schema.Field<>(root.get("time"));
    }

    /**
     * 业务类型
     * int(11)
     */
    public Schema.Field<Integer> bizType(){
        return new Schema.Field<>(root.get("bizType"));
    }

    /**
     * 业务编码
     * varchar(20)
     */
    public Schema.Field<String> bizId(){
        return new Schema.Field<>(root.get("bizId"));
    }

    /**
     * 转账金额
     * int(11)
     */
    public Schema.Field<Integer> amount(){
        return new Schema.Field<>(root.get("amount"));
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
    public static Specification<Transfer> specify(Schema.PredicateBuilder<TransferSchema> builder){
        return (root, criteriaQuery, criteriaBuilder) -> {
            TransferSchema transfer = new TransferSchema(root, criteriaBuilder);
            criteriaQuery.where(builder.build(transfer));
            return null;
        };
    }

}
