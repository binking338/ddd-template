package com.abc.dddtemplate.domain.aggregates.relationsamples.many2many;
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
 * 学生
 * @author <template/>
 * @date
 */
@AggregateRoot
@Entity
@Table(name = "`m2m_student`")
@DynamicInsert
@DynamicUpdate

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Student extends BaseEntity {

    // 【字段映射开始】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Long id;


    /**
     * 学生名称
     * varchar(100)
     */
    @Column(name = "`name`")
    private String name;

    @ManyToMany(mappedBy = "students", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER) @Fetch(FetchMode.SUBSELECT)
    private java.util.List<com.abc.dddtemplate.domain.aggregates.relationsamples.many2many.Course> courses;

    // 【字段映射结束】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动
}

