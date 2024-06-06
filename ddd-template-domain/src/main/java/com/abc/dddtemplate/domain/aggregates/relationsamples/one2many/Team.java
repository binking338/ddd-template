package com.abc.dddtemplate.domain.aggregates.relationsamples.one2many;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Fetch;
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
public class Team extends com.abc.dddtemplate.convention.BaseEntity {

    // 【字段映射开始】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    Long id;


    /**
     * 团队名称
     * varchar(100)
     */
    @Column(name = "`name`")
    String name;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true) @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "`team_id`", nullable = false)
    private java.util.List<com.abc.dddtemplate.domain.aggregates.relationsamples.one2many.Member> members;

    // 【字段映射结束】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动
}

