package com.abc.dddtemplate.convention;

import com.abc.dddtemplate.share.annotation.DomainEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;

/**
 * 领域事件发布
 *
 * @author <template/>
 * @date
 */
public interface DomainEventPublisher {

    /**
     * 添加事件
     *
     * @param event
     */
    void attachEvent(Object event);

    /**
     * 移除事件
     *
     * @param event
     */
    void detachEvent(Object event);

    /**
     * 重置，清空待发送的事件列表
     */
    void reset();

    /**
     * 判断是否集成事件
     *
     * @param event
     * @return
     */
    static boolean isIntergrationEvent(Object event) {
        if (Objects.isNull(event)) {
            throw new NullPointerException("event 参数不能为null");
        }
        DomainEvent domainEvent = event.getClass().getAnnotation(DomainEvent.class);
        if (Objects.isNull(domainEvent)) {
            throw new RuntimeException("event 必须包含 @DomainEvent 注解");
        }
        return StringUtils.isNotBlank(domainEvent.value());
    }

    class Factory{
        public static DomainEventPublisher create(Object entity) {
            return publisherFactory.apply(entity);
        }

        private static Function<Object, DomainEventPublisher> publisherFactory = null;
        public static void setFactoryMethord(Function<Object, DomainEventPublisher>  factory){
            publisherFactory = factory;
        }
    }


}
