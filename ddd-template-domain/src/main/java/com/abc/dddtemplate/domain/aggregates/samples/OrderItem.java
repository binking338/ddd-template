package com.abc.dddtemplate.domain.aggregates.samples;

import com.abc.dddtemplate.share.annotation.AggregateRoot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * 订单项
 * @author <template/>
 * @date
 */
@Entity
@Table(name = "`order_item`")
@DynamicInsert
@DynamicUpdate
@SQLDelete(sql = "update `order_item` set `db_deleted` = 1 where id = ? ")
@Where(clause = "`db_deleted` = 0")

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class OrderItem extends com.abc.dddtemplate.convention.BaseEntity {

    // 【字段映射开始】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    Long id;


    /**
     * 订单项名称
     * varchar(100)
     */
    @Column(name = "`name`")
    String name;

    /**
     * 单价
     * int(11)
     */
    @Column(name = "`price`")
    Integer price;

    /**
     * 数量
     * int(11)
     */
    @Column(name = "`num`")
    Integer num;

    // 【字段映射结束】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动
}

