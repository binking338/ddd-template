package com.abc.dddtemplate.domain.aggregates.samples;

import com.abc.dddtemplate.convention.BaseEntity;
import com.abc.dddtemplate.share.annotation.AggregateRoot;
import com.abc.dddtemplate.domain.events.internal.OrderClosedDomainEvent;
import com.abc.dddtemplate.domain.events.internal.OrderPlacedDomainEvent;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 订单
 *
 * @author <template/>
 * @date
 */
@AggregateRoot
@Entity
@Table(name = "`order`")
@DynamicInsert
@DynamicUpdate
@SQLDelete(sql = "update `order` set `db_deleted` = 1 where id = ? and `version` = ? ")
@Where(clause = "`db_deleted` = 0")

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Order extends BaseEntity {

    /**
     * 下单
     *
     * @param price
     * @param itemName
     * @return
     */
    public static Order placeOrder(String owner, String itemName, Integer price, Integer num) {
        Order order = Order.builder()
                .owner(owner)
                .name(itemName)
                .amount(0)
                .closed(false)
                .finished(false)
                .orderItems(new ArrayList<>())
                .build();
        order.addItem(itemName, price, num);
        order.publisher().attachEvent(OrderPlacedDomainEvent.builder().order(order).build());
        return order;
    }

    public List<OrderItem> clearItem() {
        List<OrderItem> itemList = new ArrayList<>(this.orderItems);
        this.orderItems.clear();
        this.amount = 0;
        return itemList;
    }

    public void addItem(String name, int price, int num) {
        if (closed || finished) {
            return;
        }
        this.orderItems.add(OrderItem.builder()
                .name(name)
                .price(price)
                .num(num)
                .build());
        this.amount += price * num;
    }

    public void finish() {
        if (!finished) {
            finished = true;
        }
    }

    public void close() {
        if (!finished) {
            closed = true;
            publisher().attachEvent(OrderClosedDomainEvent.builder().order(this).build());
        }
    }

    // 【字段映射开始】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Long id;


    /**
     * 订单金额
     * int(11)
     */
    @Column(name = "`amount`")
    private Integer amount;

    /**
     * 订单标题
     * varchar(100)
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 下单人
     * varchar(100)
     */
    @Column(name = "`owner`")
    private String owner;

    /**
     * 是否完成
     * bit(1)
     */
    @Column(name = "`finished`")
    private Boolean finished;

    /**
     * 是否关闭
     * bit(1)
     */
    @Column(name = "`closed`")
    private Boolean closed;

    /**
     * datetime
     */
    @Column(name = "`update_at`", insertable = false, updatable = true)
    private java.util.Date updateAt;

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true) @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "`order_id`", nullable = false)
    private java.util.List<com.abc.dddtemplate.domain.aggregates.samples.OrderItem> orderItems;

    /**
     * 数据版本（支持乐观锁）
     */
    @Version
    @Column(name = "`version`")
    private Integer version;

    // 【字段映射结束】本段落由[gen-ddd-maven-plugin]维护，请不要手工改动
}

