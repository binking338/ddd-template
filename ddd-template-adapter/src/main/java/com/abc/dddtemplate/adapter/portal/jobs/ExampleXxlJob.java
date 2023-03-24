package com.abc.dddtemplate.adapter.portal.jobs;

import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 基于XxlJob的任务处理逻辑，一般用于需要定时处理的场景，比如达成业务最终一致性的补偿类逻辑。
 * @author <template/>
 * @date
 */
@Slf4j
@Service
public class ExampleXxlJob {

//    @XxlJob("ddd-template-XxlExampleJob")
    public void execute() throws Exception {
    }
}
