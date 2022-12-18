package com.abc.dddtemplate.convention;

import com.abc.dddtemplate.share.annotation.AggregateRoot;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * @author <template/>
 * @date
 */
@AggregateRoot
@Entity
@Table(name = "`__archived_event`")
@DynamicInsert
@DynamicUpdate

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Slf4j
public class ArchivedEvent {

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Id
    @Column(name = "`id`")
    private Long id;

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
    @Convert(converter = Event.EventState.Converter.class)
    private Event.EventState eventState;

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
}
