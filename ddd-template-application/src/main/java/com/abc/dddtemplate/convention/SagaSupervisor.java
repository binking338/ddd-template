package com.abc.dddtemplate.convention;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <template/>
 * @date
 */
@Service
public class SagaSupervisor {
    private Map<Class, SagaStateMachine> sagaStateMachines;

    public SagaSupervisor(List<SagaStateMachine> sagaStateMachines) {
        this.sagaStateMachines = sagaStateMachines.stream().collect(Collectors.toMap(ssm -> ssm.getContextClass(), ssm -> ssm));
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
        if (!this.sagaStateMachines.containsKey(context.getClass())) {
            throw new IllegalArgumentException("context 传入参数类型不支持");
        }
        SagaStateMachine<Context> sagaStateMachine = sagaStateMachines.get(context.getClass());
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
        if (!this.sagaStateMachines.containsKey(contextClass)) {
            throw new IllegalArgumentException("contextClass 传入参数类型不支持");
        }
        SagaStateMachine<Context> sagaStateMachine = sagaStateMachines.get(contextClass);
        Saga saga = sagaStateMachine.resume(sagaId);
        return saga;
    }
}
