package com.abc.dddtemplate.domain.aggregates.samples;

import com.abc.dddtemplate.convention.BaseEntity;
import com.abc.dddtemplate.share.annotation.AggregateRoot;
import com.abc.dddtemplate.share.exception.ErrorException;
import com.abc.dddtemplate.domain.events.internal.AccountTransferDomainEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * 账户
 * @author <template/>
 * @date
 */
@AggregateRoot
@Entity
@Table(name = "`account`")
@DynamicInsert
@DynamicUpdate
@SQLDelete(sql = "update `account` set `db_deleted` = 1 where id = ? and `version` = ? ")
@Where(clause = "`db_deleted` = 0")

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Account extends BaseEntity {

    /**
     * 开户
     *
     * @param name
     * @return
     */
    public static Account open(String name) {
        return Account.builder()
                .name(name)
                .amount(0)
                .build();
    }

    public void use(Integer bizType, Long bizId, int amount){
        if(amount > this.amount){
            throw new ErrorException("账户余额不够");
        }
        this.amount -= amount;
        this.publisher().attachEvent(AccountTransferDomainEvent.builder()
                .accountId(id)
                .bizType(bizType)
                .bizId(bizId.toString())
                .amount(-amount)
                .build()
        );
    }

    public void charge(String transferNo, int amount){
        this.amount += amount;
        this.publisher().attachEvent(AccountTransferDomainEvent.builder()
                .accountId(id)
                .bizType(Transfer.CHARGE)
                .bizId(transferNo)
                .amount(amount)
                .build()
        );
    }

    // 【字段映射开始】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Long id;


    /**
     * 账户名称
     * varchar(100)
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 账户余额
     * int(11)
     */
    @Column(name = "`amount`")
    private Integer amount;

    /**
     * 数据版本（支持乐观锁）
     */
    @Version
    @Column(name = "`version`")
    private Integer version;

    // 【字段映射结束】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动
}

