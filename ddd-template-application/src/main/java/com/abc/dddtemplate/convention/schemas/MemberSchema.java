package com.abc.dddtemplate.convention.schemas;

import com.abc.dddtemplate.convention.Schema;
import com.abc.dddtemplate.domain.aggregates.relationsamples.one2many.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * 成员 
 * 本文件由[gen-ddd-maven-plugin]生成
 */
@RequiredArgsConstructor
public class MemberSchema {
    private final Root<Member> root;
    private final CriteriaBuilder criteriaBuilder;

    public CriteriaBuilder criteriaBuilder(){
        return criteriaBuilder;
    }

    public Schema.Field<Long> id(){
        return new Schema.Field<>(root.get("id"));
    }

    /**
     * 成员名称
     * varchar(100)
     */
    public Schema.Field<String> name(){
        return new Schema.Field<>(root.get("name"));
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
    public static Specification<Member> specify(Schema.PredicateBuilder<MemberSchema> builder){
        return (root, criteriaQuery, criteriaBuilder) -> {
            MemberSchema member = new MemberSchema(root, criteriaBuilder);
            criteriaQuery.where(builder.build(member));
            return null;
        };
    }

}
