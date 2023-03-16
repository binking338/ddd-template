package com.abc.dddtemplate.adapter.domain.convention;

import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.convention.aggregates.ArchivedSaga;

/**
 * @author qiaohe
 * @date 2023-03-16
 */
public interface ArchivedSagaRepository extends AggregateRepository<ArchivedSaga, Long> {
}
