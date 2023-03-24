package com.abc.dddtemplate.adapter.portal.queues;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import static com.abc.dddtemplate.share.Constants.QUEUE_EXAMPLE_CONSUMER_GROUP;
import static com.abc.dddtemplate.share.Constants.QUEUE_EXAMPLE_TOPIC;

/**
 * 消息队列，用于需要长时间处理的任务队列消费。（长时间处理的任务如果CPU或内存需求比较大，建议单独开启进程）
 * @author <template/>
 * @date
 */
@Slf4j
@Component
//@RocketMQMessageListener(topic = QUEUE_EXAMPLE_TOPIC, consumerGroup = QUEUE_EXAMPLE_CONSUMER_GROUP)
public class ExampleQueueConsumer implements RocketMQListener<String> {
    @Override
    public void onMessage(String s) {

    }
}
