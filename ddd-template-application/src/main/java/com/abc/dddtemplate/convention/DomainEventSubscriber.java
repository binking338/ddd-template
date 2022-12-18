package com.abc.dddtemplate.convention;

/**
 * 领域事件订阅消费
 *
 * @author <template/>
 * @date
 */
public interface DomainEventSubscriber<T> {

    /**
     * 订阅的领域事件类型
     *
     * @return
     */
    Class<T> forEventClass();

    /**
     * 领域事件消费逻辑
     *
     * @param event
     */
    void onEvent(T event);
}
