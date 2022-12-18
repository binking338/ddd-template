package com.abc.dddtemplate.convention;

import com.abc.dddtemplate.share.annotation.DomainEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

/**
 * 领域事件控制器
 */
@Service
public class DomainEventSupervisor {

    public DomainEventSupervisor(
            AggregateRepository<Event, Long> eventRepository,
            @Autowired(required = false) List<DomainEventSubscriber<?>> subscribers) {
        setEventPersistanceHandler(event -> eventRepository.saveAndFlush(event));
        setSubscribers(subscribers);
        DomainEventPublisher.Factory.setFactoryMethord(entity -> new DefaultDomainEventPublisher(entity));
        instance = this;
    }

    private Consumer<Event> eventPersistanceHandler = null;
    private Map<Class<?>, List<DomainEventSubscriber<?>>> subscribersMap = new HashMap<>();
    private static DomainEventSupervisor instance;
    private static final ThreadLocal<List<Event>> threadLocalDispatchedIntergrationEvents = new ThreadLocal<>();

    public static List<Event> getDispatchedIntergrationEvents() {
        return threadLocalDispatchedIntergrationEvents.get();
    }

    public static List<Object> fireAttachedEvents() {
        List<DefaultDomainEventPublisher> domainEventPublishers = DefaultDomainEventPublisher.getDomainEventPublishers();
        List<Object> publishedEvents = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(domainEventPublishers)) {
            List<DefaultDomainEventPublisher> clonedPublishers = new ArrayList<>(domainEventPublishers);
            for (DefaultDomainEventPublisher publisher : clonedPublishers) {
                List<Object> events = publisher.fireAttachedEvents(instance::dispatchOnce);
                publishedEvents.addAll(events);
            }
        }
        return Collections.unmodifiableList(publishedEvents);
    }

    /**
     * 立即发送传入的 event 领域事件
     * @param event
     * @param <T>
     */
    public <T> void dispatchOnce(T event) {
        if (Objects.isNull(event)) {
            throw new NullPointerException("param event is null");
        }

        dispatch2LocalSubscriber(event);
        if (DomainEventPublisher.isIntergrationEvent(event)) {
            dispatch2RemoteSubscriber(event);
        }
    }

    private void dispatch2RemoteSubscriber(Object event) {
        Date now = new Date();
        Event intergrationEvent = new Event();
        intergrationEvent.init(now, Duration.ofDays(1), 100);
        intergrationEvent.loadPayload(event);

        eventPersistanceHandler.accept(intergrationEvent);

        List<Event> dispatchedEvents = threadLocalDispatchedIntergrationEvents.get();
        if (dispatchedEvents == null) {
            dispatchedEvents = new ArrayList<>();
            threadLocalDispatchedIntergrationEvents.set(dispatchedEvents);
        }
        dispatchedEvents.add(intergrationEvent);
    }

    private <T> void dispatch2LocalSubscriber(T event) {
        if (subscribersMap == null || subscribersMap.size() == 0) {
            throw new RuntimeException("没有配置领域事件订阅器，调用前需通过 DomainEventSubscriber.Supervisor.setSubscribers 方法配置。");
        }
        if (!subscribersMap.containsKey(event.getClass())) {
            return;
        }
        List<DomainEventSubscriber<?>> domainEventSubscribers = subscribersMap.get(event.getClass());
        domainEventSubscribers.forEach(s -> ((DomainEventSubscriber<T>) s).onEvent(event));
    }

    private void setEventPersistanceHandler(Consumer<Event> handler) {
        eventPersistanceHandler = handler;
    }

    private void setSubscribers(List<DomainEventSubscriber<?>> subscribers) {
        if (CollectionUtils.isNotEmpty(subscribers)) {
            for (DomainEventSubscriber subscriber : subscribers) {
                if (!subscribersMap.containsKey(subscriber.forEventClass())) {
                    subscribersMap.put(subscriber.forEventClass(), new ArrayList<>());
                }
                subscribersMap.get(subscriber.forEventClass()).add(subscriber);
            }
        }
    }


    @Service
    @Scope("prototype")
    public static class DefaultDomainEventPublisher implements DomainEventPublisher {

        private final List<Object> events = new ArrayList<>();
        private final Object entity;

        public DefaultDomainEventPublisher() {
            this(null);
        }

        public DefaultDomainEventPublisher(Object entity) {
            this.entity = entity;
        }

        protected static final ThreadLocal<List<DefaultDomainEventPublisher>> threadLocalDomainEventPublishers = new ThreadLocal<>();
        protected static final ThreadLocal<Map<Object, DefaultDomainEventPublisher>> threadLocalDomainEventPublisherMap = new ThreadLocal<>();

        protected static List<DefaultDomainEventPublisher> getDomainEventPublishers() {
            List<DefaultDomainEventPublisher> domainEventPublishers = threadLocalDomainEventPublishers.get();
            if (domainEventPublishers != null) {
                domainEventPublishers = Collections.unmodifiableList(domainEventPublishers);
            } else {
                domainEventPublishers = Collections.EMPTY_LIST;
            }
            return domainEventPublishers;
        }

        protected static DefaultDomainEventPublisher getEntityAttachedDomainEventPublisher(Object entity) {
            Map<Object, DefaultDomainEventPublisher> entityEventPublisherMap = threadLocalDomainEventPublisherMap.get();
            if (entityEventPublisherMap == null) {
                return null;
            }
            return entityEventPublisherMap.get(entity);
        }

        protected static void addDomainEventPublisher(DefaultDomainEventPublisher publisher) {
            List<DefaultDomainEventPublisher> domainEventPublishers = threadLocalDomainEventPublishers.get();
            if (domainEventPublishers == null) {
                domainEventPublishers = new ArrayList<>();
                threadLocalDomainEventPublishers.set(domainEventPublishers);
            }
            if (domainEventPublishers.contains(publisher)) {
                return;
            }
            domainEventPublishers.add(publisher);

            if (publisher.entity != null) {
                Map<Object, DefaultDomainEventPublisher> entityEventPublisherMap = threadLocalDomainEventPublisherMap.get();
                if (entityEventPublisherMap == null) {
                    entityEventPublisherMap = new HashMap<>();
                    threadLocalDomainEventPublisherMap.set(entityEventPublisherMap);
                }
                entityEventPublisherMap.put(publisher.entity, publisher);
            }
        }

        protected static void removeDomainEventPublisher(DefaultDomainEventPublisher publisher) {
            List<DefaultDomainEventPublisher> domainEventPublishers = threadLocalDomainEventPublishers.get();
            if (domainEventPublishers == null) {
                return;
            }
            domainEventPublishers.remove(publisher);

            if (publisher.entity != null) {
                Map<Object, DefaultDomainEventPublisher> entityEventPublisherMap = threadLocalDomainEventPublisherMap.get();
                if (entityEventPublisherMap != null) {
                    entityEventPublisherMap.remove(publisher);
                }
            }
        }

        @Override
        public void attachEvent(Object event) {
            if (event.getClass().getAnnotation(DomainEvent.class) == null) {
                throw new IllegalArgumentException("event参数必须是有@DomainEvent注解的对象");
            }
            events.add(event);
            addDomainEventPublisher(this);
        }

        @Override
        public void detachEvent(Object event) {
            events.remove(event);
        }

        @Override
        public void reset() {
            events.clear();
            removeDomainEventPublisher(this);
        }

        /**
         * 分发当前上下文的事件
         */
        protected List<Object> fireAttachedEvents(Consumer<Object> dispatch) {
            List<Object> publishedEvents = new ArrayList<>();
            while (events.size() > 0) {
                Object event = events.get(0);
                events.remove(event);
                dispatch.accept(event);
                publishedEvents.add(event);
            }
            if (events.size() == 0) {
                removeDomainEventPublisher(this);
            }
            return Collections.unmodifiableList(publishedEvents);
        }
    }
}

