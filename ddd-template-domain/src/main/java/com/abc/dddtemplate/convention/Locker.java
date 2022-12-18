package com.abc.dddtemplate.convention;

import com.abc.dddtemplate.share.annotation.AggregateRoot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.Duration;
import java.util.Date;

/**
 * @author <template/>
 * @date
 */
@AggregateRoot
@Entity
@Table(name = "`__locker`")
@DynamicInsert
@DynamicUpdate

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Slf4j
public class Locker {
    public boolean acquire(String pwd, Duration lockDuration, Date now) {
        if (this.unlockAt.before(now)) {
            this.pwd = pwd;
            this.lockAt = now;
            this.unlockAt = DateUtils.addSeconds(now, (int) lockDuration.getSeconds());
            return true;
        } else if (StringUtils.compare(pwd, this.pwd) == 0) {
            this.unlockAt = DateUtils.addSeconds(now, (int) lockDuration.getSeconds());
            return true;
        } else {
            return false;
        }
    }

    public boolean release(String pwd, Date now) {
        if (StringUtils.compare(pwd, this.pwd) == 0) {
            this.unlockAt = now;
            return true;
        } else {
            return false;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "`id`")
    private Long id;

    /**
     * 锁名称
     * varchar(100)
     */
    @Column(name = "`name`")
    private String name;

    /**
     * 锁口令
     * varchar(100)
     */
    @Column(name = "`pwd`")
    private String pwd;

    /**
     * 锁时间
     * datetime
     */
    @Column(name = "`lock_at`")
    private Date lockAt;

    /**
     * 释放时间
     * datetime
     */
    @Column(name = "`unlock_at`")
    private Date unlockAt;

    /**
     * 乐观锁
     * int
     */
    @Version
    @Column(name = "`version`")
    private Long version;

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
