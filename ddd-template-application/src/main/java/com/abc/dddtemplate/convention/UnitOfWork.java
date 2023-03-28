package com.abc.dddtemplate.convention;

import com.abc.dddtemplate.convention.aggregates.Event;
import com.abc.dddtemplate.share.CodeEnum;
import com.abc.dddtemplate.share.exception.KnownException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <template/>
 * @date
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UnitOfWork {

    /**
     * UoW事务成功提交事件
     */
    public static class TransactionCommittedEvent extends ApplicationEvent {
        @Getter
        List<Event> events;

        /**
         * Create a new {@code ApplicationEvent}.
         *
         * @param source the object on which the event initially occurred or with
         *               which the event is associated (never {@code null})
         */
        public TransactionCommittedEvent(Object source, List<Event> events) {
            super(source);
            this.events = events;
        }
    }


    /**
     * 领域事件需要发送
     */
    public static class DomainEventFireEvent extends ApplicationEvent {
        /**
         * Create a new {@code ApplicationEvent}.
         *
         * @param source the object on which the event initially occurred or with
         *               which the event is associated (never {@code null})
         */
        public DomainEventFireEvent(Object source) {
            super(source);
        }
    }

    /**
     * 事务执行句柄
     */
    public interface TransactionHandler {
        void exec();
    }

    /**
     * 事务执行句柄
     */
    public interface TransactionHandlerWithOutput<T> {
        T exec();
    }

    /**
     * 事务执行句柄
     */
    public interface TransactionHandlerWithInputOutput<I, O> {
        O exec(I input);
    }

    @Getter
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired(required = false)
    private List<Specification> specifications;
    private ConcurrentHashMap<Class, List<Specification>> specificationsClassMap = new ConcurrentHashMap<>();
    private ThreadLocal<Map<Object, Boolean>> preValidatedThreadLocal = ThreadLocal.withInitial(() -> new HashMap<>());
    private ThreadLocal<Map<Object, Boolean>> postValidatedThreadLocal = ThreadLocal.withInitial(() -> new HashMap<>());
    private ThreadLocal<Integer> stackDepthCounterThreadLocal = ThreadLocal.withInitial(() -> 0);
    private ThreadLocal<Set<Object>> additionalAttachedEntitiesThreadLocal = ThreadLocal.withInitial(() -> new HashSet<>());
    private ThreadLocal<Set<Object>> attachedEntitiesThreadLocal = ThreadLocal.withInitial(() -> new HashSet<>());
    private ThreadLocal<Set<Object>> removedEntitiesThreadLocal = ThreadLocal.withInitial(() -> new HashSet<>());

    /**
     * 移除上下文
     */
    public void clear() {
        preValidatedThreadLocal.remove();
        postValidatedThreadLocal.remove();
        stackDepthCounterThreadLocal.remove();
        additionalAttachedEntitiesThreadLocal.remove();
        attachedEntitiesThreadLocal.remove();
        removedEntitiesThreadLocal.remove();
    }

    /**
     * 将实体附加到UoW上下文
     *
     * @param entities
     */
    public void attach(Object... entities) {
        attach(Arrays.stream(entities).collect(Collectors.toList()));
    }

    /**
     * 将实体附加到UoW上下文
     *
     * @param entities
     */
    public void attach(Collection<?> entities) {
        Set<Object> attachedEntities = attachedEntitiesThreadLocal.get();
        attachedEntities.addAll(entities);
    }

    /**
     * 将欲删除的实体附加到UoW上下文
     *
     * @param entities
     */
    public void remove(Object... entities) {
        remove(Arrays.stream(entities).collect(Collectors.toList()));
    }

    /**
     * 将欲删除的实体附加到UoW上下文
     *
     * @param entities
     */
    public void remove(Collection<?> entities) {
        Set<Object> removedEntities = removedEntitiesThreadLocal.get();
        removedEntities.addAll(entities);
    }

    /**
     * 事务删除
     *
     * @param entities
     */
    public void delete(Object... entities) {
        delete(entities.length > 0 ? Arrays.stream(entities).collect(Collectors.toList()) : null);
    }

    /**
     * 事务删除
     *
     * @param entities
     */
    public void delete(Collection<?> entities) {
        save(null, entities);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param entities 待持久化的实体
     */
    public void save(Object... entities) {
        save(entities.length > 0 ? Arrays.stream(entities).collect(Collectors.toList()) : null);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param entities 待持久化的实体
     */
    public void save(Collection<?> entities) {
        save(entities, null);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param saveEntities   保存实体
     * @param deleteEntities 删除实体
     */
    public void save(Collection<?> saveEntities, Collection<?> deleteEntities) {
        List<?> saveEntityList = CollectionUtils.isNotEmpty(saveEntities)
                ? Stream.concat(attachedEntitiesThreadLocal.get().stream(), saveEntities.stream())
                .collect(Collectors.toList())
                : attachedEntitiesThreadLocal.get().stream().collect(Collectors.toList());
        List<?> deleteEntityList = CollectionUtils.isNotEmpty(deleteEntities)
                ? Stream.concat(removedEntitiesThreadLocal.get().stream(), deleteEntities.stream())
                .collect(Collectors.toList())
                : removedEntitiesThreadLocal.get().stream().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(saveEntityList)) {
            additionalAttachedEntitiesThreadLocal.get().addAll(saveEntityList);
        }
        if (CollectionUtils.isNotEmpty(deleteEntityList)) {
            additionalAttachedEntitiesThreadLocal.get().addAll(deleteEntityList);
        }
        save(() -> {
            if (CollectionUtils.isNotEmpty(saveEntityList)) {
                for (Object entity : saveEntityList) {
                    if (getEntityManager().contains(entity)) {
                        getEntityManager().merge(entity);
                        getEntityManager().flush();
                    } else {
                        try {
                            Object id = entity.getClass().getMethod("getId").invoke(entity);
                            if (id != null) {
                                getEntityManager().merge(entity);
                                getEntityManager().flush();
                                continue;
                            }
                        } catch (Exception _ex) {
                            /* we don't care */
                        }
                        getEntityManager().persist(entity);
                        getEntityManager().flush();
                        getEntityManager().refresh(entity);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(deleteEntityList)) {
                for (Object entity : deleteEntityList) {
                    if (getEntityManager().contains(entity)) {
                        getEntityManager().remove(entity);
                    } else {
                        getEntityManager().remove(getEntityManager().merge(entity));
                    }
                }
            }
        });
        additionalAttachedEntitiesThreadLocal.remove();
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param transactionHandler
     */
    public void save(TransactionHandler transactionHandler) {
        // 集成事件
        save(transactionHandler, Propagation.REQUIRED);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param transactionHandler
     */
    public void save(TransactionHandler transactionHandler, Propagation propagation) {
        save(publisher -> {
            transactionHandler.exec();
            return null;
        }, propagation);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param transactionHandler
     * @param <T>
     */
    public <T> T save(TransactionHandlerWithOutput<T> transactionHandler) {
        // 集成事件
        return save(transactionHandler, Propagation.REQUIRED);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param transactionHandler
     * @param <T>
     */
    public <T> T save(TransactionHandlerWithOutput<T> transactionHandler, Propagation propagation) {
        T result = null;
        result = save(publisher -> transactionHandler.exec(), propagation);

        return result;
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param transactionHandler
     * @param <T>
     */
    public <T> T save(TransactionHandlerWithInputOutput<DomainEventPublisher, T> transactionHandler) {
        return save(transactionHandler, Propagation.REQUIRED);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param transactionHandler
     * @param <T>
     */
    public <T> T save(TransactionHandlerWithInputOutput<DomainEventPublisher, T> transactionHandler, Propagation propagation) {
        T result = null;
        stackDepthCounterThreadLocal.set(stackDepthCounterThreadLocal.get() + 1);
        preTransactionSpecifications();
        switch (propagation) {
            case SUPPORTS:
                result = instance.supports(transactionHandler, DomainEventPublisher.Factory.create(null));
                break;
            case NOT_SUPPORTED:
                result = instance.notSupported(transactionHandler, DomainEventPublisher.Factory.create(null));
                break;
            case REQUIRES_NEW:
                result = instance.requiresNew(transactionHandler, DomainEventPublisher.Factory.create(null));
                break;
            case MANDATORY:
                result = instance.mandatory(transactionHandler, DomainEventPublisher.Factory.create(null));
                break;
            case NEVER:
                result = instance.never(transactionHandler, DomainEventPublisher.Factory.create(null));
                break;
            case NESTED:
                result = instance.nested(transactionHandler, DomainEventPublisher.Factory.create(null));
                break;
            case REQUIRED:
            default:
                result = instance.required(transactionHandler, DomainEventPublisher.Factory.create(null));
                break;
        }
        stackDepthCounterThreadLocal.set(stackDepthCounterThreadLocal.get() - 1);
        if (stackDepthCounterThreadLocal.get().equals(0)) {
            preValidatedThreadLocal.get().clear();
            postValidatedThreadLocal.get().clear();
        }
        return result;
    }

    protected List<Object> correlatedEntities() {
        Stream<Object> entities = additionalAttachedEntitiesThreadLocal.get().stream();
        try {
            if (!((SessionImplementor) getEntityManager().getDelegate()).isClosed()) {

                org.hibernate.engine.spi.PersistenceContext persistenceContext = ((SessionImplementor) getEntityManager().getDelegate()).getPersistenceContext();
                Stream<Object> entitiesInPersistenceContext = Arrays.stream(persistenceContext.reentrantSafeEntityEntries()).map(e -> e.getKey());
                entities = Stream.concat(entities, entitiesInPersistenceContext);
            }
        } catch (Exception ex) {
            log.debug("跟踪实体获取失败", ex);
        }
        return entities.distinct().collect(Collectors.toList());
    }

    protected List<Specification> getSpecificationsForEntityClass(Class clazz) {
        if (CollectionUtils.isEmpty(specifications)) {
            return null;
        }
        if (specificationsClassMap.contains(clazz)) {
            return specificationsClassMap.get(clazz);
        }
        List<Specification> list = new ArrayList<>();
        for (Specification spec : specifications) {
            if (spec.entityClass().isAssignableFrom(clazz)) {
                list.add(spec);
            }
        }
        specificationsClassMap.putIfAbsent(clazz, list);
        return list;
    }

    protected void preTransactionSpecifications() {
        List<Object> entities = correlatedEntities();
        for (Object entity : entities) {
            if (preValidatedThreadLocal.get().containsKey(entity)) {
                continue;
            }
            List<Specification> specs = getSpecificationsForEntityClass(entity.getClass());
            if (CollectionUtils.isNotEmpty(specs)) {
                for (Specification spec : specs) {
                    if (!spec.inTransaction()) {
                        if (!spec.valid(entity)) {
                            throw new KnownException(CodeEnum.SPECIFICATION_UNSATISFIED.getCode(), spec.failMsg(entity));
                        }
                    }
                }
            }
            preValidatedThreadLocal.get().putIfAbsent(entity, true);
        }
    }

    protected void inTransactionSpecifications() {
        List<Object> entities = correlatedEntities();
        for (Object entity : entities) {
            if (postValidatedThreadLocal.get().containsKey(entity)) {
                continue;
            }
            List<Specification> specs = getSpecificationsForEntityClass(entity.getClass());
            if (CollectionUtils.isNotEmpty(specs)) {
                for (Specification spec : specs) {
                    if (spec.inTransaction()) {
                        if (!spec.valid(entity)) {
                            throw new KnownException(CodeEnum.SPECIFICATION_UNSATISFIED.getCode(), spec.failMsg(entity));
                        }
                    }
                }
            }
            postValidatedThreadLocal.get().putIfAbsent(entity, true);
        }
    }

    /**
     * 清除上下文
     */
    public static void clearContext() {
        instance.clear();
    }

    /**
     * 将实体附加到UoW上下文
     *
     * @param entities
     */
    public static void attachEntities(Object... entities) {
        instance.attach(entities);
    }

    /**
     * 将实体附加到UoW上下文
     *
     * @param entities
     */
    public static void attachEntities(Collection<?> entities) {
        instance.attach(entities);
    }

    /**
     * 将欲删除的实体附加到UoW上下文
     *
     * @param entities
     */
    public static void removeEntities(Object... entities) {
        instance.remove(entities);
    }

    /**
     * 将欲删除的实体附加到UoW上下文
     *
     * @param entities
     */
    public static void removeEntities(Collection<?> entities) {
        instance.remove(entities);
    }

    /**
     * 事务删除
     *
     * @param entities
     */
    public static void deleteEntities(Object... entities) {
        instance.delete(entities);
    }

    /**
     * 事务删除
     *
     * @param entities
     */
    public static void deleteEntities(Collection<?> entities) {
        instance.delete(entities);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param saveEntities   保存实体
     * @param deleteEntities 删除实体
     */
    public static void saveEntities(Collection<?> saveEntities, Collection<?> deleteEntities) {
        instance.save(saveEntities, deleteEntities);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param entities 待持久化的实体
     */
    public static void saveEntities(Object... entities) {
        instance.save(entities);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param entities 待持久化的实体
     */
    public static void saveEntities(Collection<?> entities) {
        instance.save(entities);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param transactionHandler
     * @return
     */
    public static void saveTransactional(TransactionHandler transactionHandler) {
        instance.save(transactionHandler);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param transactionHandler
     * @return
     */
    public static void saveTransactional(TransactionHandler transactionHandler, Propagation propagation) {
        instance.save(transactionHandler, propagation);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param transactionHandler
     * @param <T>
     * @return
     */
    public static <T> T saveTransactional(TransactionHandlerWithOutput<T> transactionHandler) {
        return instance.save(transactionHandler);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param transactionHandler
     * @param <T>
     * @return
     */
    public static <T> T saveTransactional(TransactionHandlerWithOutput<T> transactionHandler, Propagation propagation) {
        return instance.save(transactionHandler, propagation);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param transactionHandler
     * @param <T>
     * @return
     */
    public static <T> T saveTransactional(TransactionHandlerWithInputOutput<DomainEventPublisher, T> transactionHandler) {
        return instance.save(transactionHandler);
    }

    /**
     * 事务保存，自动发送领域事件
     *
     * @param transactionHandler
     * @param <T>
     * @return
     */
    public static <T> T saveTransactional(TransactionHandlerWithInputOutput<DomainEventPublisher, T> transactionHandler, Propagation propagation) {
        return instance.save(transactionHandler, propagation);
    }

    /**
     * 获取JPA EntityManager
     *
     * @return
     */
    public static EntityManager entityManager() {
        return instance.getEntityManager();
    }

    private static UnitOfWork instance;

    @Service
    private static class UnitOfWorkStaticInstanceLoader {
        public UnitOfWorkStaticInstanceLoader(UnitOfWork unitOfWork) {
            UnitOfWork.instance = unitOfWork;
        }
    }

    private final ApplicationEventPublisher applicationEventPublisher;

    private void transaction(TransactionHandler transactionHandler) {
        inTransactionSpecifications();
        if (transactionHandler != null) {
            transactionHandler.exec();
        }
        applicationEventPublisher.publishEvent(new DomainEventFireEvent(this));
    }

    private <T> T transactionWithOutput(TransactionHandlerWithOutput<T> transactionHandler) {
        T result = null;
        inTransactionSpecifications();
        if (transactionHandler != null) {
            result = transactionHandler.exec();
        }
        applicationEventPublisher.publishEvent(new DomainEventFireEvent(this));
        return result;
    }

    private <I, O> O transactionWithInputOutput(TransactionHandlerWithInputOutput<I, O> transactionHandler, I in) {
        O result = null;
        inTransactionSpecifications();
        if (transactionHandler != null) {
            result = transactionHandler.exec(in);
        }
        applicationEventPublisher.publishEvent(new DomainEventFireEvent(this));
        return result;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void required(TransactionHandler transactionHandler) {
        transaction(transactionHandler);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public <T> T required(TransactionHandlerWithOutput<T> transactionHandler) {
        return transactionWithOutput(transactionHandler);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public <I, O> O required(TransactionHandlerWithInputOutput<I, O> transactionHandler, I in) {
        return transactionWithInputOutput(transactionHandler, in);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void requiresNew(TransactionHandler transactionHandler) {
        transaction(transactionHandler);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public <T> T requiresNew(TransactionHandlerWithOutput<T> transactionHandler) {
        return transactionWithOutput(transactionHandler);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public <I, O> O requiresNew(TransactionHandlerWithInputOutput<I, O> transactionHandler, I in) {
        return transactionWithInputOutput(transactionHandler, in);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
    public void supports(TransactionHandler transactionHandler) {
        transaction(transactionHandler);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
    public <T> T supports(TransactionHandlerWithOutput<T> transactionHandler) {
        return transactionWithOutput(transactionHandler);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.SUPPORTS)
    public <I, O> O supports(TransactionHandlerWithInputOutput<I, O> transactionHandler, I in) {
        return transactionWithInputOutput(transactionHandler, in);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
    public void notSupported(TransactionHandler transactionHandler) {
        transaction(transactionHandler);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
    public <T> T notSupported(TransactionHandlerWithOutput<T> transactionHandler) {
        return transactionWithOutput(transactionHandler);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NOT_SUPPORTED)
    public <I, O> O notSupported(TransactionHandlerWithInputOutput<I, O> transactionHandler, I in) {
        return transactionWithInputOutput(transactionHandler, in);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.MANDATORY)
    public void mandatory(TransactionHandler transactionHandler) {
        transaction(transactionHandler);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.MANDATORY)
    public <T> T mandatory(TransactionHandlerWithOutput<T> transactionHandler) {
        return transactionWithOutput(transactionHandler);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.MANDATORY)
    public <I, O> O mandatory(TransactionHandlerWithInputOutput<I, O> transactionHandler, I in) {
        return transactionWithInputOutput(transactionHandler, in);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NEVER)
    public void never(TransactionHandler transactionHandler) {
        transaction(transactionHandler);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NEVER)
    public <T> T never(TransactionHandlerWithOutput<T> transactionHandler) {
        return transactionWithOutput(transactionHandler);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NEVER)
    public <I, O> O never(TransactionHandlerWithInputOutput<I, O> transactionHandler, I in) {
        return transactionWithInputOutput(transactionHandler, in);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public void nested(TransactionHandler transactionHandler) {
        transaction(transactionHandler);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public <T> T nested(TransactionHandlerWithOutput<T> transactionHandler) {
        return transactionWithOutput(transactionHandler);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public <I, O> O nested(TransactionHandlerWithInputOutput<I, O> transactionHandler, I in) {
        return transactionWithInputOutput(transactionHandler, in);
    }

}
