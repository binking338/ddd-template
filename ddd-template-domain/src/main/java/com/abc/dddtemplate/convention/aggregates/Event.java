package com.abc.dddtemplate.convention.aggregates;

import com.abc.dddtemplate.share.annotation.AggregateRoot;
import com.abc.dddtemplate.share.annotation.DomainEvent;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * @author <template/>
 * @date
 */
@AggregateRoot
@Entity
@Table(name = "`__event`")
@DynamicInsert
@DynamicUpdate

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Slf4j
public class Event {

    public void init(Date now, String svcName, Object payload, Duration expireAfter, int retryTimes) {
        this.eventUuid = UUID.randomUUID().toString();
        this.svcName = svcName;
        this.createAt = now;
        this.expireAt = DateUtils.addSeconds(now, (int) expireAfter.getSeconds());
        this.eventState = Event.EventState.INIT;
        this.tryTimes = retryTimes;
        this.triedTimes = 1;
        this.lastTryTime = now;
        this.loadPayload(payload);
        this.nextTryTime = calculateNextTryTime(now);
    }

    @Transient
    private Object payload = null;

    public Object restorePayload() {
        if (this.payload != null) {
            return this.payload;
        }
        Class dataClass = null;
        try {
            dataClass = Class.forName(dataType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            log.error("事件解析错误", e);
        }
        this.payload = JSON.parseObject(data, dataClass);
        return this.payload;
    }

    private void loadPayload(Object payload) {
        this.payload = payload;
        this.data = JSON.toJSONString(payload);
        this.dataType = payload.getClass().getName();
        DomainEvent domainEvent = payload.getClass().getAnnotation(DomainEvent.class);
        this.eventType = domainEvent.value();
        this.tryTimes = domainEvent.retryTimes();
        this.expireAt = DateUtils.addSeconds(this.createAt, domainEvent.expireAfter());
    }


    public boolean tryDelivery(Date now) {
        if (this.triedTimes >= this.tryTimes) {
            this.eventState = EventState.FAILED;
            return false;
        }
        if (now.after(this.expireAt)) {
            this.eventState = EventState.EXPIRED;
            return false;
        }
        if (!EventState.INIT.equals(this.eventState)
                && !EventState.COMFIRMING.equals(this.eventState)) {
            return false;
        }
        if (this.nextTryTime.after(now)) {
            return false;
        }
        this.eventState = EventState.COMFIRMING;
        this.lastTryTime = now;
        this.nextTryTime = calculateNextTryTime(now);
        this.triedTimes++;
        return true;
    }

    private Date calculateNextTryTime(Date now) {
        DomainEvent domainEvent = restorePayload().getClass().getAnnotation(DomainEvent.class);
        if (Objects.isNull(restorePayload()) || Objects.isNull(domainEvent) || domainEvent.retryIntervals().length == 0) {
            if (this.triedTimes <= 3) {
                return DateUtils.addSeconds(now, 10);
            } else if (this.triedTimes <= 6) {
                return DateUtils.addSeconds(now, 30);
            } else if (this.triedTimes <= 10) {
                return DateUtils.addSeconds(now, 60);
            } else if (this.triedTimes <= 20) {
                return DateUtils.addMinutes(now, 5);
            } else {
                return DateUtils.addMinutes(now, 10);
            }
        }
        int index = this.triedTimes - 1;
        if (index >= domainEvent.retryIntervals().length) {
            index = domainEvent.retryIntervals().length - 1;
        } else if (index < 0) {
            index = 0;
        }
        return DateUtils.addSeconds(now, domainEvent.retryIntervals()[index]);
    }

    public void comfirmedDelivered() {
        this.eventState = EventState.DELIVERED;
    }

    public void cancel() {
        this.eventState = EventState.CANCEL;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Long id;

    /**
     * 事件uuid
     * varchar(64)
     */
    @Column(name = "`event_uuid`")
    private String eventUuid;

    /**
     * 服务
     * varchar
     */
    @Column(name = "`svc_name`")
    private String svcName;

    /**
     * 事件类型
     * varchar(100)
     */
    @Column(name = "`event_type`")
    private String eventType;

    /**
     * 事件数据
     * varchar(1000)
     */
    @Column(name = "`data`")
    private String data;

    /**
     * 事件数据类型
     * varchar(200)
     */
    @Column(name = "`data_type`")
    private String dataType;

    /**
     * 创建时间
     * datetime
     */
    @Column(name = "`create_at`")
    private Date createAt;

    /**
     * 过期时间
     * datetime
     */
    @Column(name = "`expire_at`")
    private Date expireAt;

    /**
     * 分发状态
     * int
     */
    @Column(name = "`event_state`")
    @Convert(converter = EventState.Converter.class)
    private EventState eventState;

    /**
     * 尝试次数
     * int
     */
    @Column(name = "`try_times`")
    private Integer tryTimes;

    /**
     * 已尝试次数
     * int
     */
    @Column(name = "`tried_times`")
    private Integer triedTimes;

    /**
     * 上次尝试时间
     * datetime
     */
    @Column(name = "`last_try_time`")
    private Date lastTryTime;

    /**
     * 下次尝试时间
     * datetime
     */
    @Column(name = "`next_try_time`")
    private Date nextTryTime;

    /**
     * 乐观锁
     * int
     */
    @Version
    @Column(name = "`version`")
    private Integer version;

    /**
     * 创建时间
     * datetime
     */
    @Column(name = "`db_created_at`", insertable = false, updatable = false)
    private Date dbCreatedAt;

    /**
     * 更新时间
     * datetime
     */
    @Column(name = "`db_updated_at`", insertable = false, updatable = false)
    private Date dbUpdatedAt;

    @AllArgsConstructor
    public enum EventState {
        /**
         * 初始状态
         */
        INIT(0, "init"),
        /**
         * 待确认发送结果
         */
        COMFIRMING(-1, "comfirming"),
        /**
         * 业务主动取消
         */
        CANCEL(-2, "cancel"),
        /**
         * 过期
         */
        EXPIRED(-3, "expired"),
        /**
         * 用完重试次数
         */
        FAILED(-4, "failed"),
        /**
         * 已发送
         */
        DELIVERED(1, "delivered");
        @Getter
        private final Integer value;
        @Getter
        private final String name;

        public static EventState valueOf(Integer value) {
            for (EventState val : EventState.values()) {
                if (Objects.equals(val.value, value)) {
                    return val;
                }
            }
            throw new RuntimeException("枚举类型DeliveryState枚举值转换异常，不存在的值" + value);
        }

        public static class Converter implements AttributeConverter<EventState, Integer> {

            @Override
            public Integer convertToDatabaseColumn(EventState attribute) {
                return attribute.value;
            }

            @Override
            public EventState convertToEntityAttribute(Integer dbData) {
                return EventState.valueOf(dbData);
            }
        }
    }
}
