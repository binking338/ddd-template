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
 * 成员
 * @author
 * @date
 */
@Entity
@Table(name = "`o2m_member`")
@DynamicInsert
@DynamicUpdate

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Member extends BaseEntity {

    // 【字段映射开始】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Long id;


    /**
     * 成员名称
     * varchar(100)
     */
    @Column(name = "`name`")
    private String name;

    // 【字段映射结束】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动
}

