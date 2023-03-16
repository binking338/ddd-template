package com.abc.dddtemplate.domain.aggregates.relationsamples.many2many;
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
import java.util.ArrayList;

/**
 * 课程
 * @author <template/>
 * @date
 */
@AggregateRoot
@Entity
@Table(name = "`m2m_course`")
@DynamicInsert
@DynamicUpdate

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Course extends BaseEntity {

    public void attach(Student student) {
        if (students == null) {
            students = new ArrayList<>();
        }
        students.add(student);
    }

    // 【字段映射开始】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Long id;


    /**
     * 课程名称
     * varchar(100)
     */
    @Column(name = "`name`")
    private String name;

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER) @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "`m2m_student_course_rel`", joinColumns = {@JoinColumn(name = "`course_id`")}, inverseJoinColumns = {@JoinColumn(name = "`student_id`")})
    private java.util.List<com.abc.dddtemplate.domain.aggregates.relationsamples.many2many.Student> students;

    // 【字段映射结束】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动
}

