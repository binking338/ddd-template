package com.abc.dddtemplate.convention.schemas;

import com.abc.dddtemplate.convention.Schema;
import com.abc.dddtemplate.domain.aggregates.relationsamples.one2many.Team;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 团队 
 * 本文件由[gen-ddd-maven-plugin]生成
 * 警告：请勿手工修改该文件，重新生成会覆盖该文件
 */
@RequiredArgsConstructor
public class TeamSchema {
    private final Path<Team> root;
    private final CriteriaBuilder criteriaBuilder;

    public CriteriaBuilder criteriaBuilder() {
        return criteriaBuilder;
    }

    public Schema.Field<Long> id() {
        return root == null ? new Schema.Field<>("id") : new Schema.Field<>(root.get("id"));
    }

    /**
     * 团队名称
     * varchar(100)
     */
    public Schema.Field<String> name() {
        return root == null ? new Schema.Field<>("name") : new Schema.Field<>(root.get("name"));
    }

    /**
     * 满足所有条件
     * @param restrictions
     * @return
     */
    public Predicate all(Predicate... restrictions) {
        return criteriaBuilder().and(restrictions);
    }

    /**
     * 满足任一条件
     * @param restrictions
     * @return
     */
    public Predicate any(Predicate... restrictions) {
        return criteriaBuilder().or(restrictions);
    }

    /**
     * 指定条件
     * @param builder
     * @return
     */
    public Predicate spec(Schema.PredicateBuilder<TeamSchema> builder){
        return builder.build(this);
    }

    /**
     * Member 关联查询条件定义
     *
     * @param joinType
     * @return
     */
    public MemberSchema joinMember(Schema.JoinType joinType) {
        JoinType type = transformJoinType(joinType);
        Join<Team, com.abc.dddtemplate.domain.aggregates.relationsamples.one2many.Member> join = ((Root<Team>) root).join("members", type);
        MemberSchema schema = new MemberSchema(join, criteriaBuilder);
        return schema;
    }


    private JoinType transformJoinType(Schema.JoinType joinType){
        if(joinType == Schema.JoinType.INNER){
            return JoinType.INNER;
        } else if(joinType == Schema.JoinType.LEFT){
            return JoinType.LEFT;
        } else if(joinType == Schema.JoinType.RIGHT){
            return JoinType.RIGHT;
        }
        return JoinType.LEFT;
    }

    /**
     * 构建查询条件
     * @param builder
     * @param distinct
     * @return
     */
    public static Specification<Team> specify(Schema.PredicateBuilder<TeamSchema> builder, boolean distinct) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            TeamSchema team = new TeamSchema(root, criteriaBuilder);
            criteriaQuery.where(builder.build(team));
            criteriaQuery.distinct(distinct);
            return null;
        };
    }
    
    /**
     * 构建查询条件
     * @param builder
     * @return
     */
    public static Specification<Team> specify(Schema.PredicateBuilder<TeamSchema> builder) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            TeamSchema team = new TeamSchema(root, criteriaBuilder);
            criteriaQuery.where(builder.build(team));
            return null;
        };
    }
    
    /**
     * 构建排序
     * @param builders
     * @return
     */
    public static Sort orderBy(Schema.OrderBuilder<TeamSchema>... builders) {
        return orderBy(Arrays.asList(builders));
    }

    /**
     * 构建排序
     *
     * @param builders
     * @return
     */
    public static Sort orderBy(Collection<Schema.OrderBuilder<TeamSchema>> builders) {
        if(CollectionUtils.isEmpty(builders)) {
            return Sort.unsorted();
        }
        return Sort.by(builders.stream()
                .map(builder -> builder.build(new TeamSchema(null, null)))
                .collect(Collectors.toList())
        );
    }

}
