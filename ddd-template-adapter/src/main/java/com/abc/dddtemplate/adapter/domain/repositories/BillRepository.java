package com.abc.dddtemplate.adapter.domain.repositories;

import com.abc.dddtemplate.convention.AggregateRepository;
import com.abc.dddtemplate.domain.aggregates.samples.Bill;

import java.util.Optional;

/**
 * 本文件由[gen-ddd-maven-plugin]生成，请不要手工改动
 */
public interface BillRepository extends AggregateRepository<Bill, Long> {
    Optional<Bill> findByOrderId(Long orderId);
}
