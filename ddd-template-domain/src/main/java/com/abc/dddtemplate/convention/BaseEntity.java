package com.abc.dddtemplate.convention;

/**
 * 实体
 *
 * @author <template/>
 * @date
 */
public abstract class BaseEntity {

    private DomainEventPublisher domainEventPublisher;

    public DomainEventPublisher publisher() {
        if (domainEventPublisher == null) {
            domainEventPublisher = DomainEventPublisher.Factory.create(this);
        }
        return domainEventPublisher;
    }
}
