package com.abc.dddtemplate.convention;

import com.abc.dddtemplate.convention.aggregates.Saga;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <template/>
 * @date
 */
@Service
@Slf4j
public class SagaSupervisor {
    private Map<Class, SagaStateMachine> sagaStateMachineContextClassMap;
    private Map<Integer, SagaStateMachine> sagaStateMachineBizTypeMap;

    public SagaSupervisor(List<SagaStateMachine> sagaStateMachineContextClassMap) {
        if (CollectionUtils.isNotEmpty(sagaStateMachineContextClassMap)) {
            this.sagaStateMachineContextClassMap = sagaStateMachineContextClassMap.stream().collect(Collectors.toMap(ssm -> ssm.getContextClass(), ssm -> ssm));
            this.sagaStateMachineBizTypeMap = sagaStateMachineContextClassMap.stream().collect(Collectors.toMap(ssm -> ssm.getBizType(), ssm -> ssm));
        } else {
            this.sagaStateMachineContextClassMap = Collections.emptyMap();
            this.sagaStateMachineBizTypeMap = Collections.emptyMap();
        }
    }

    /**
     * 获取支持的bizType
     *
     * @return
     */
    public Set<Integer> getSupportedBizTypes() {
        return this.sagaStateMachineBizTypeMap.keySet();
    }

    /**
     * 获取支持的上下文类型
     *
     * @return
     */
    public Set<Class> getSupportedContextClasses() {
        return this.sagaStateMachineContextClassMap.keySet();
    }

    /**
     * 执行Saga流程
     *
     * @param context
     * @param <Context>
     * @return
     */
    public <Context> Saga run(Context context) {
        return run(context, true);
    }

    /**
     * 执行Saga流程
     *
     * @param context
     * @param runImmediately
     * @param <Context>
     * @return
     */
    public <Context> Saga run(Context context, boolean runImmediately) {
        Assert.notNull(context, "coontext 参数不得为空");
        if (!this.sagaStateMachineContextClassMap.containsKey(context.getClass())) {
            throw new IllegalArgumentException("context 传入参数类型不支持");
        }
        SagaStateMachine<Context> sagaStateMachine = sagaStateMachineContextClassMap.get(context.getClass());
        Saga saga = sagaStateMachine.run(context, runImmediately);
        return saga;
    }

    /**
     * 恢复Saga流程
     *
     * @param contextClass
     * @param sagaId
     * @param <Context>
     * @return
     */
    public <Context> Saga resume(Class<Context> contextClass, Long sagaId) {
        if (!this.sagaStateMachineContextClassMap.containsKey(contextClass)) {
            throw new IllegalArgumentException("contextClass 传入参数类型不支持");
        }
        SagaStateMachine<Context> sagaStateMachine = sagaStateMachineContextClassMap.get(contextClass);
        Saga saga = sagaStateMachine.resume(sagaId);
        return saga;
    }

    /**
     * 恢复Saga流程
     *
     * @param saga
     * @param <Context>
     * @return
     */
    public <Context> Saga resume(Saga saga) {
        Assert.notNull(saga, "saga 参数不得为空");
        SagaStateMachine<Context> sagaStateMachine = sagaStateMachineBizTypeMap.get(saga.getBizType());
        if (!sagaStateMachine.getContextClass().getName().equalsIgnoreCase(saga.getContextDataType())) {
            throw new UnsupportedOperationException("bizType不匹配 sagaId=" + saga.getId());
        }
        Saga newSaga = sagaStateMachine.resume(saga);
        return newSaga;
    }

    /**
     * 回滚Saga流程
     *
     * @param contextClass
     * @param sagaId
     * @param <Context>
     * @return
     */
    public <Context> Saga rollback(Class<Context> contextClass, Long sagaId) {
        if (!this.sagaStateMachineContextClassMap.containsKey(contextClass)) {
            throw new IllegalArgumentException("contextClass 传入参数类型不支持");
        }
        SagaStateMachine<Context> sagaStateMachine = sagaStateMachineContextClassMap.get(contextClass);
        Saga saga = sagaStateMachine.rollback(sagaId);
        return saga;
    }

    /**
     * 回滚Saga流程
     *
     * @param saga
     * @param <Context>
     * @return
     */
    public <Context> Saga rollback(Saga saga) {
        Assert.notNull(saga, "saga 参数不得为空");
        SagaStateMachine<Context> sagaStateMachine = sagaStateMachineBizTypeMap.get(saga.getBizType());
        if (!sagaStateMachine.getContextClass().getName().equalsIgnoreCase(saga.getContextDataType())) {
            throw new UnsupportedOperationException("bizType不匹配 sagaId=" + saga.getId());
        }
        Saga newSaga = sagaStateMachine.rollback(saga);
        return newSaga;
    }
}
