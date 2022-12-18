package com.abc.dddtemplate.convention.schemas;

import com.abc.dddtemplate.convention.Schema;
import com.abc.dddtemplate.domain.aggregates.relationsamples.many2many.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;

/**
 * 本文件由[gen-ddd-maven-plugin]生成
 */
@RequiredArgsConstructor
public class TeacherSchema {
    private final Root<Course> root;
    private final CriteriaBuilder criteriaBuilder;

    public CriteriaBuilder criteriaBuilder(){
        return criteriaBuilder;
    }

    public Schema.Field<Long> id(){
        return new Schema.Field<>(root.get("id"));
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
    public static Specification<Course> specify(Schema.PredicateBuilder<TeacherSchema> builder){
        return (root, criteriaQuery, criteriaBuilder) -> {
            TeacherSchema Teacher = new TeacherSchema(root, criteriaBuilder);
            criteriaQuery.where(builder.build(Teacher));
            return null;
        };
    }

}
