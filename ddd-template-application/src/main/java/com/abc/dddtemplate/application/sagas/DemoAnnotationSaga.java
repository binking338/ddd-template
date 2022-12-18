package com.abc.dddtemplate.application.sagas;

import com.abc.dddtemplate.convention.SagaStateMachine;
import com.abc.dddtemplate.convention.annotation.SagaProcess;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author qiaohe
 * @date 2022-12-18
 */
@Data
public class DemoAnnotationSaga {

    String input;

    String output;

    @Service
    @Slf4j
    public static class Handler extends SagaStateMachine<DemoAnnotationSaga> {
        @Override
        protected Integer getBizType() {
            return 2;
        }

        @Override
        protected Class<DemoAnnotationSaga> getContextClass() {
            return DemoAnnotationSaga.class;
        }

        @SagaProcess
        public void step1(DemoAnnotationSaga context){
            context.output += "step1 finished!";
        }

        @SagaProcess(parent = "step1")
        public void step1_1(DemoAnnotationSaga context) {
            context.output += "step1_1 finished!";
        }

        @SagaProcess(parent = "step1")
        public void step1_2(DemoAnnotationSaga context) {
            context.output += "step1_2 finished!";
        }

        @SagaProcess(preview = "step1")
        public void step2(DemoAnnotationSaga context) {
            context.output += "step2 finished!";
        }

        @SagaProcess(parent = "step2")
        public void step2_1(DemoAnnotationSaga context) {
            context.output += "step2_1 finished!";
        }

        @SagaProcess(preview = "step2")
        public void step3(DemoAnnotationSaga context) {
            context.output += "step3 finished!";
        }
    }
}
