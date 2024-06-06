package com.abc.dddtemplate.domain.aggregates.samples;

import com.abc.dddtemplate.share.annotation.AggregateRoot;
import com.abc.dddtemplate.domain.aggregates.samples.events.external.BillPaidDomainEvent;
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
 * 账单
 * @author <template/>
 * @date
 */
@AggregateRoot
@Entity
@Table(name = "`bill`")
@DynamicInsert
@DynamicUpdate
@SQLDelete(sql = "update `bill` set `db_deleted` = 1 where id = ? and `version` = ? ")
@Where(clause = "`db_deleted` = 0")

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Bill extends com.abc.dddtemplate.convention.BaseEntity {

    public void pay(Integer amount) {
        if (!this.getClosed() && !this.getPayed() && this.getAmount() <= amount) {
            payed = true;
            this.publisher().attachEvent(BillPaidDomainEvent.builder().bill(this).build());
        }
    }

    public void close() {
        if (!getPayed()) {
            closed = true;
        }
    }

    // 【字段映射开始】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    Long id;


    /**
     * bigint(20)
     */
    @Column(name = "`order_id`")
    Long orderId;

    /**
     * 账单名称
     * varchar(100)
     */
    @Column(name = "`name`")
    String name;

    /**
     * 支付人
     * varchar(100)
     */
    @Column(name = "`owner`")
    String owner;

    /**
     * 账单金额
     * int(11)
     */
    @Column(name = "`amount`")
    Integer amount;

    /**
     * 是否支付
     * bit(1)
     */
    @Column(name = "`payed`")
    Boolean payed;

    /**
     * 是否关闭
     * bit(1)
     */
    @Column(name = "`closed`")
    Boolean closed;

    /**
     * 数据版本（支持乐观锁）
     */
    @Version
    @Column(name = "`version`")
    Integer version;

    // 【字段映射结束】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动
}

