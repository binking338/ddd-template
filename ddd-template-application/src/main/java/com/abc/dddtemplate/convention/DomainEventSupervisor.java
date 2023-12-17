package com.abc.dddtemplate.convention;

import com.abc.dddtemplate.convention.aggregates.Event;
import com.abc.dddtemplate.share.annotation.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 领域事件控制器
 */
@Service
@Slf4j
public class DomainEventSupervisor {
    @Value("${app.id:[default]}")
    protected String svcName;

    public DomainEventSupervisor(
            AggregateRepository<Event, Long> eventRepository,
            ApplicationEventPublisher applicationEventPublisher,
            @Autowired(required = false) List<DomainEventSubscriber<?>> subscribers) {
        setEventPersistanceHandler(event -> eventRepository.saveAndFlush(event));
        this.applicationEventPublisher = applicationEventPublisher;
        setSubscribers(subscribers);
        DomainEventPublisher.Factory.setFactoryMethord(entity -> new DefaultDomainEventPublisher(entity));
        instance = this;
    }

    private final ApplicationEventPublisher applicationEventPublisher;

    private Function<Event, Event> eventPersistanceHandler = null;
    private Map<Class<?>, List<DomainEventSubscriber<?>>> subscribersMap = new HashMap<>();
    private static DomainEventSupervisor instance;
    private static final ThreadLocal<List<Event>> threadLocalDispatchedIntergrationEvents = new ThreadLocal<>();

    static List<Object> fireAttachedEvents() {
        List<DefaultDomainEventPublisher> domainEventPublishers = DefaultDomainEventPublisher.getDomainEventPublishers();
        List<Object> publishedEvents = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(domainEventPublishers)) {
            List<DefaultDomainEventPublisher> clonedPublishers = new ArrayList<>(domainEventPublishers);
            for (DefaultDomainEventPublisher publisher : clonedPublishers) {
                List<Object> events = publisher.fireAttachedEvents(instance::dispatchRawImmediately);
                if (CollectionUtils.isNotEmpty(events)) {
                    publishedEvents.addAll(events);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(publishedEvents)) {
            return Collections.unmodifiableList(publishedEvents);
        } else {
            return Collections.emptyList();
        }
    }

    public static void clearDispatchedIntergrationEvents() {
        threadLocalDispatchedIntergrationEvents.remove();
    }

    @EventListener
    private void onDomainEventFireEvent(UnitOfWork.DomainEventFireEvent event) {
        fireAttachedEvents();
        List<Event> intergrationEvents = threadLocalDispatchedIntergrationEvents.get();
        if (CollectionUtils.isNotEmpty(intergrationEvents)) {
            threadLocalDispatchedIntergrationEvents.remove();
            UnitOfWork.TransactionCommittedEvent transactionCommittedEvent =
                    new UnitOfWork.TransactionCommittedEvent(this, intergrationEvents);
            applicationEventPublisher.publishEvent(transactionCommittedEvent);
        }
    }

    /**
     * 立即发送传入的 event 领域事件
     *
     * @param event
     * @param <T>
     */
    public <T> void dispatchRawImmediately(T event) {
        dispatchRawImmediately(event, false);
    }

    /**
     * 立即发送传入的 event 领域事件
     *
     * @param event
     * @param forceLocal
     * @param <T>
     */
    public <T> void dispatchRawImmediately(T event, boolean forceLocal) {
        if (Objects.isNull(event)) {
            throw new NullPointerException("param event is null");
        }

        if (!forceLocal && DomainEventPublisher.isIntergrationEvent(event)) {
            dispatch2LocalSubscriber(event);
            dispatch2RemoteSubscriber(event);
        } else {
            dispatch2LocalSubscriber(event);
        }
    }


    /**
     * 立即发送传入的集成事件
     *
     * @param events
     * @return
     */
    public int dispatchIntergrationEventImmediately(List<Event> events) {
        List<Event> retryEvents = new ArrayList<>(10);
        int failedCount = 0;
        for (Event event : events) {
            try {
                Date now = new Date();
                if (!event.tryDelivery(now)) {
                    eventPersistanceHandler.apply(event);
                    continue;
                } else {
                    event = eventPersistanceHandler.apply(event);
                    retryEvents.add(event);
                }
            } catch (Exception ex) {
                // 数据库并发异常
                failedCount++;
                log.error("集成事件补偿发送-持久化失败", ex);
            }
        }

        UnitOfWork.TransactionCommittedEvent transactionCommittedEvent = new UnitOfWork.TransactionCommittedEvent(this, retryEvents);
        applicationEventPublisher.publishEvent(transactionCommittedEvent);
        return failedCount;
    }

    private void dispatch2RemoteSubscriber(Object event) {
        Date now = new Date();
        Event intergrationEvent = new Event();
        intergrationEvent.init(now, svcName, event, Duration.ofDays(1), 100);

        intergrationEvent = eventPersistanceHandler.apply(intergrationEvent);

        List<Event> dispatchedEvents = threadLocalDispatchedIntergrationEvents.get();
        if (dispatchedEvents == null) {
            dispatchedEvents = new ArrayList<>();
            threadLocalDispatchedIntergrationEvents.set(dispatchedEvents);
        }
        dispatchedEvents.add(intergrationEvent);
    }

    private <T> void dispatch2LocalSubscriber(T event) {
        applicationEventPublisher.publishEvent(event);
        if (subscribersMap == null || subscribersMap.size() == 0) {
            return;
        }
        if (!subscribersMap.containsKey(event.getClass())) {
            return;
        }
        List<DomainEventSubscriber<?>> domainEventSubscribers = subscribersMap.get(event.getClass());
        domainEventSubscribers.forEach(s -> ((DomainEventSubscriber<T>) s).onEvent(event));
    }

    private void setEventPersistanceHandler(Function<Event, Event> handler) {
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
            if (CollectionUtils.isNotEmpty(publishedEvents)) {
                return Collections.unmodifiableList(publishedEvents);
            } else {
                return Collections.emptyList();
            }
        }
    }
}

