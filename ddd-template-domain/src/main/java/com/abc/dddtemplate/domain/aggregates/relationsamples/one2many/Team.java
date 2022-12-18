package com.abc.dddtemplate.domain.aggregates.relationsamples.one2many;
import com.abc.dddtemplate.convention.BaseEntity;
import com.abc.dddtemplate.share.annotation.AggregateRoot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * 团队
 * @author
 * @date
 */
@AggregateRoot
@Entity
@Table(name = "`o2m_team`")
@DynamicInsert
@DynamicUpdate

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Team extends BaseEntity {

    // 【字段映射开始】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Long id;


    /**
     * 团队名称
     * varchar(100)
     */
    @Column(name = "`name`")
    private String name;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "`team_id`", nullable = false)
    private java.util.List<com.abc.dddtemplate.domain.aggregates.relationsamples.one2many.Member> members;

    // 【字段映射结束】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动
}

