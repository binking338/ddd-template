package com.abc.dddtemplate.convention.aggregates;

import com.abc.dddtemplate.share.annotation.AggregateRoot;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author <template/>
 * @date
 */
@AggregateRoot
@Entity
@Table(name = "`__saga`")
@DynamicInsert
@DynamicUpdate

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Saga {

    public void init(Date now, String svcName, Integer bizType, Object context, Class contextClass, Date nextTryTime, int expireInSeconds, int retryTimes, List<SagaProcess> sagaProcesses) {
        this.svcName = svcName;
        this.bizType = bizType;
        this.contextData = (JSON.toJSONString(context));
        this.contextDataType = contextClass.getName();
        this.sagaState = Saga.SagaState.INIT;
        this.createAt = now;
        this.expireAt = DateUtils.addSeconds(now, expireInSeconds);
        this.tryTimes = retryTimes;
        this.triedTimes = 0;
        this.lastTryTime = now;
        this.nextTryTime = nextTryTime;
        this.processes = sagaProcesses;
    }

    public boolean startRunning(Date now, Date nextTryTime) {
        if (triedTimes >= tryTimes) {
            this.sagaState = SagaState.FAILED;
            return false;
        }
        if (expireAt.before(now)) {
            this.sagaState = SagaState.EXPIRED;
            return false;
        }
        if (!SagaState.INIT.equals(this.sagaState)
                && (!SagaState.RUNNING.equals(this.sagaState) || this.nextTryTime.after(now))) {
            return false;
        }
        this.sagaState = SagaState.RUNNING;
        this.triedTimes++;
        this.lastTryTime = now;
        this.nextTryTime = nextTryTime;
        return true;
    }

    public void finishRunning(Object context) {
        this.contextData = JSON.toJSONString(context);
        this.sagaState = SagaState.DONE;
    }

    public void cancel() {
        this.sagaState = SagaState.CANCEL;
    }

    public void fail(Object context) {
        this.contextData = JSON.toJSONString(context);
        if (triedTimes >= tryTimes) {
            this.sagaState = SagaState.FAILED;
        } else {
            this.sagaState = SagaState.RUNNING;
        }
    }

    public boolean startRollback() {
        this.sagaState = SagaState.ROLLBACKING;
        return true;
    }

    public void finishRollback(Object context) {
        this.contextData = JSON.toJSONString(context);
        this.sagaState = SagaState.ROLLBACKED;
    }

    public SagaProcess findProcess(Integer processCode) {
        return processes.stream().filter(p -> (Objects.equals(p.processCode, processCode))).findFirst().orElse(null);
    }

    public <Ctx> Ctx getContext(Class<Ctx> ctxClass) {
        return JSON.parseObject(contextData, ctxClass);
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
     * ????????????
     * int
     */
    @Column(name = "`biz_type`")
    private Integer bizType;

    /**
     * ??????
     * varchar
     */
    @Column(name = "`svc_name`")
    private String svcName;

    /**
     * ?????????
     * varchar
     */
    @Column(name = "`context_data`")
    private String contextData;

    /**
     * ?????????????????????
     * varchar
     */
    @Column(name = "`context_data_type`")
    private String contextDataType;

    /**
     * ????????????
     * int
     */
    @Column(name = "`saga_state`")
    @Convert(converter = SagaState.Converter.class)
    private SagaState sagaState;

    /**
     * ????????????
     * datetime
     */
    @Column(name = "`expire_at`")
    private Date expireAt;

    /**
     * ????????????
     * datetime
     */
    @Column(name = "`create_at`")
    private Date createAt;

    /**
     * ????????????
     * int
     */
    @Column(name = "`try_times`")
    private Integer tryTimes;

    /**
     * ???????????????
     * int
     */
    @Column(name = "`tried_times`")
    private Integer triedTimes;

    /**
     * ??????????????????
     * int
     */
    @Column(name = "`last_try_time`")
    private Date lastTryTime;

    /**
     * ??????????????????
     * datetime
     */
    @Column(name = "`next_try_time`")
    private Date nextTryTime;

    /**
     * ????????????
     */
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "`saga_id`", nullable = false)
    private java.util.List<SagaProcess> processes;

    /**
     * ?????????
     * int
     */
    @Version
    @Column(name = "`version`")
    private Integer version;

    /**
     * ????????????
     * datetime
     */
    @Column(name = "`db_created_at`", insertable = false, updatable = false)
    private Date dbCreatedAt;

    /**
     * ????????????
     * datetime
     */
    @Column(name = "`db_updated_at`", insertable = false, updatable = false)
    private Date dbUpdatedAt;


    @Entity
    @Table(name = "`__saga_process`")
    @DynamicInsert
    @DynamicUpdate

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    public static class SagaProcess {
        public void init(Date now, Integer code, String name) {
            this.processCode = code;
            this.processName = name;
            this.contextData = "";
            this.processState = Saga.SagaState.INIT;
            this.triedTimes = 0;
            this.lastTryTime = now;
            this.createAt = now;
            this.exception = "";
        }

        /**
         * @param now
         * @return
         */
        public boolean startRunning(Date now, Object context) {
            if (SagaState.INIT.equals(this.processState)
                    || SagaState.FAILED.equals(this.processState)
                    || SagaState.RUNNING.equals(this.processState)) {
                this.contextData = JSON.toJSONString(context);
                this.processState = SagaState.RUNNING;
                this.lastTryTime = now;
                this.triedTimes++;
                return true;
            }
            return false;
        }

        public void finishRunning(Object context) {
            this.contextData = JSON.toJSONString(context);
            this.processState = SagaState.DONE;
        }

        public void startRollback(Object context) {
            this.contextData = JSON.toJSONString(context);
            this.processState = SagaState.ROLLBACKING;
        }

        public void finishRollback(Object context) {
            this.contextData = JSON.toJSONString(context);
            this.processState = SagaState.ROLLBACKED;
        }

        public void fail(Exception ex) {
            this.processState = SagaState.FAILED;
            this.exception = StringUtils.isEmpty(ex.getMessage()) ? "" : ex.getMessage();
        }

        public <Ctx> Ctx getContext(Class<Ctx> ctxClass) {
            return JSON.parseObject(contextData, ctxClass);
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
         * ????????????
         * int
         */
        @Column(name = "`process_code`")
        private Integer processCode;

        /**
         * ????????????
         * varchar
         */
        @Column(name = "`process_name`")
        private String processName;

        /**
         * ????????????
         * datetime
         */
        @Column(name = "`create_at`")
        private Date createAt;

        /**
         * ?????????
         * varchar
         */
        @Column(name = "`context_data`")
        private String contextData;

        /**
         * ??????????????????
         * int
         */
        @Column(name = "`process_state`")
        @Convert(converter = SagaState.Converter.class)
        private SagaState processState;

        /**
         * ???????????????
         * int
         */
        @Column(name = "`tried_times`")
        private Integer triedTimes;

        /**
         * ??????????????????
         * int
         */
        @Column(name = "`last_try_time`")
        private Date lastTryTime;

        /**
         * ????????????
         * varchar
         */
        @Column(name = "`exception`")
        private String exception;

        /**
         * ????????????
         * datetime
         */
        @Column(name = "`db_created_at`", insertable = false, updatable = false)
        private Date dbCreatedAt;

        /**
         * ????????????
         * datetime
         */
        @Column(name = "`db_updated_at`", insertable = false, updatable = false)
        private Date dbUpdatedAt;
    }

    @AllArgsConstructor
    public enum SagaState {
        /**
         * ????????????
         */
        INIT(0, "init"),
        /**
         * ?????????
         */
        RUNNING(-1, "running"),
        /**
         * ??????????????????
         */
        CANCEL(-2, "cancel"),
        /**
         * ??????
         */
        EXPIRED(-3, "expired"),
        /**
         * ??????????????????
         */
        FAILED(-4, "failed"),
        /**
         * ?????????
         */
        ROLLBACKING(-5, "rollbacking"),
        /**
         * ?????????
         */
        ROLLBACKED(-6, "rollbacked"),
        /**
         * ?????????
         */
        DONE(1, "done");
        @Getter
        private final Integer value;
        @Getter
        private final String name;

        public static SagaState valueOf(Integer value) {
            for (SagaState val : SagaState.values()) {
                if (Objects.equals(val.value, value)) {
                    return val;
                }
            }
            throw new RuntimeException("????????????DeliveryState???????????????????????????????????????" + value);
        }

        public static class Converter implements AttributeConverter<SagaState, Integer> {

            @Override
            public Integer convertToDatabaseColumn(SagaState attribute) {
                return attribute.value;
            }

            @Override
            public SagaState convertToEntityAttribute(Integer dbData) {
                return SagaState.valueOf(dbData);
            }
        }
    }
}
