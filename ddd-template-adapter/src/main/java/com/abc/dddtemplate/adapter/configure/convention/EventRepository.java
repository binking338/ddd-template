package com.abc.dddtemplate.adapter.configure.convention;

import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.aggregates.Event;

/**
 * @author <template/>
 * @date
 */

public interface EventRepository extends AggregateRepository<Event, Long> {

}
