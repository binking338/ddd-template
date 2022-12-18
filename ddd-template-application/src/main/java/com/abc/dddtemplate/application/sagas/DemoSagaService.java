package com.abc.dddtemplate.application.sagas;

import com.abc.dddtemplate.convention.SagaStateMachine;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Saga服务示例
 * 支持分布式事务场景，通过通用重试补偿机制实现最终一致性
 * @author <template/>
 * @date
 */
@Service
@Slf4j
public class DemoSagaService extends SagaStateMachine<DemoSagaService.Context> {

    @Override
    protected Integer getBizType() {
        return 1;
    }

    @Override
    protected Class<Context> getContextClass() {
        return Context.class;
    }

    @Override
    protected int getNextTryIdleInSeconds(int triedTimes) {
        return 10;
    }

    @Override
    protected Process<Context> config() {
        return Process
                .of((Context context) -> {
                    context.output = "input=" + context.getInput() + ";";
                    context.output += "process 1 done;";
                })
                .then(context -> {
                    context.output += "process 2 done;";
                    // throw new RuntimeException("异常");
                })
                .sub(
                        context -> {
                            context.output += "process 3 done;";
                        },
                        context -> {
                            context.output += "process 4 done;";
                        },
                        context -> {
                            context.output += "process 5 done;";
                        },
                        context -> {
                            context.output += "process 6 done;";
                        })
                .then(context -> {
                    context.output += "process 7 done;";
                })
                .root();

//        return Process
//                .of(10, (Context context) -> {
//                    context.output = "input=" + context.getInput() + ";";
//                    context.output += "process 1 done;";
//                }, context -> {
//                    context.output += "process 1 rollback;";
//                })
//                .then(20, context -> {
//                    context.output += "process 2 done;";
//                }, context -> {
//                    context.output += "process 2 rollback;";
//                })
//                .sub(
//                        Process.of(21, context -> {
//                            context.output += "process 2.1 done;";
//                        }, context -> {
//                            context.output += "process 2.1 rollback;";
//                        }),
//                        Process.of(22, (Context context) -> {
//                            context.output += "process 2.2 done;";
//                        }, context -> {
//                            context.output += "process 2.2 rollback;";
//                        })
//                                .sub(Process.of(23, context -> {
//                                    context.output += "process 2.3 done;";
//                                }, context -> {
//                                    context.output += "process 2.3 rollback;";
//                                })).root(),
//                        Process.of(24, context -> {
//                            context.output += "process 2.4 done;";
//                        }, context -> {
//                            context.output += "process 2.4 rollback;";
//                        }))
//                .then(30, context -> {
//                    throw new RuntimeException("some thing break");
//                }, context -> {
//                    context.output += "process 3 rollback;";
//                })
//                .root();
    }

    @Data
    public static class Context {
        private String input;
        private String output;
    }
}
