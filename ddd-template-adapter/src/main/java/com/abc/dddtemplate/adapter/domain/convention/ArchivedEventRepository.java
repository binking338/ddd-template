package com.abc.dddtemplate.adapter.domain.convention;

import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.aggregates.ArchivedEvent;

/**
 * @author <template/>
 * @date 2023-03-16
 */
public interface ArchivedEventRepository extends AggregateRepository<ArchivedEvent, Long> {
}
