package com.abc.dddtemplate.convention.schemas;

import com.abc.dddtemplate.convention.Schema;
import com.abc.dddtemplate.domain.aggregates.samples.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * 账户 
 * 本文件由[gen-ddd-maven-plugin]生成
 */
@RequiredArgsConstructor
public class AccountSchema {
    private final Root<Account> root;
    private final CriteriaBuilder criteriaBuilder;

    public CriteriaBuilder criteriaBuilder(){
        return criteriaBuilder;
    }

    public Schema.Field<Long> id(){
        return new Schema.Field<>(root.get("id"));
    }

    /**
     * 账户名称
     * varchar(100)
     */
    public Schema.Field<String> name(){
        return new Schema.Field<>(root.get("name"));
    }

    /**
     * 账户余额
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
    public static Specification<Account> specify(Schema.PredicateBuilder<AccountSchema> builder){
        return (root, criteriaQuery, criteriaBuilder) -> {
            AccountSchema account = new AccountSchema(root, criteriaBuilder);
            criteriaQuery.where(builder.build(account));
            return null;
        };
    }

}
