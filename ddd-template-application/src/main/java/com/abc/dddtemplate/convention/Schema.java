package com.abc.dddtemplate.convention;

import org.hibernate.query.criteria.internal.path.SingularAttributePath;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

/**
 * Schema
 *
 * @author <template/>
 * @date
 */
public class Schema {

    /**
     * 断言构建器
     */
    public interface PredicateBuilder<S> {
        Expression<Boolean> build(S schema);
    }

    /**
     * 字段
     *
     * @param <T>
     */
    public static class Field<T> extends SingularAttributePath<T> {

        public Field(Path<T> path) {
            super(((SingularAttributePath<T>) path).criteriaBuilder(), ((SingularAttributePath<T>) path).getJavaType(), ((SingularAttributePath<T>) path).getPathSource(), ((SingularAttributePath<T>) path).getAttribute());
        }

        public Predicate isTrue() {
            return criteriaBuilder().isTrue((Expression<Boolean>) this);
        }

        public Predicate isFalse() {
            return criteriaBuilder().isTrue((Expression<Boolean>) this);

        }

        public Predicate equal(Object val) {
            return criteriaBuilder().equal(this, val);
        }

        public Predicate equal(Expression<?> val) {
            return criteriaBuilder().equal(this, val);
        }

        public Predicate notEqual(Object val) {
            return criteriaBuilder().notEqual(this, val);
        }

        public Predicate notEqual(Expression<?> val) {
            return criteriaBuilder().notEqual(this, val);
        }

        public <Y extends Comparable<? super Y>> Predicate greaterThan(Y val) {
            return criteriaBuilder().greaterThan((Expression<Y>) this, val);
        }

        public <Y extends Comparable<? super Y>> Predicate greaterThan(Expression<? extends Y> val) {
            return criteriaBuilder().greaterThan((Expression<Y>) this, val);
        }

        public <Y extends Comparable<? super Y>> Predicate greaterThanOrEqualTo(Y val) {
            return criteriaBuilder().greaterThan((Expression<Y>) this, val);
        }

        public <Y extends Comparable<? super Y>> Predicate greaterThanOrEqualTo(Expression<? extends Y> val) {
            return criteriaBuilder().greaterThanOrEqualTo((Expression<Y>) this, val);
        }

        public <Y extends Comparable<? super Y>> Predicate lessThan(Y val) {
            return criteriaBuilder().lessThan((Expression<Y>) this, val);
        }

        public <Y extends Comparable<? super Y>> Predicate lessThan(Expression<? extends Y> val) {
            return criteriaBuilder().lessThan((Expression<Y>) this, val);
        }

        public <Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Y val) {
            return criteriaBuilder().lessThanOrEqualTo((Expression<Y>) this, val);
        }

        public <Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Expression<? extends Y> val) {
            return criteriaBuilder().lessThanOrEqualTo((Expression<Y>) this, val);
        }

        public <Y extends Comparable<? super Y>> Predicate between(Y val1, Y val2) {
            return criteriaBuilder().between((Expression<Y>) this, val1, val2);
        }

        public <Y extends Comparable<? super Y>> Predicate between(Expression<? extends Y> val1, Expression<? extends Y> val2) {
            return criteriaBuilder().between((Expression<Y>) this, val1, val2);
        }


        public Predicate like(String val) {
            return criteriaBuilder().like((Expression<String>) this, val);
        }

        public Predicate like(Expression<String> val) {
            return criteriaBuilder().like((Expression<String>) this, val);
        }

        public Predicate notLike(String val) {
            return criteriaBuilder().notLike((Expression<String>) this, val);
        }

        public Predicate notLike(Expression<String> val) {
            return criteriaBuilder().notLike((Expression<String>) this, val);
        }


        public Predicate eq(Object val) {
            return equal(val);
        }

        public Predicate eq(Expression<?> val) {
            return equal(val);
        }

        public Predicate neq(Object val) {
            return notEqual(val);
        }

        public Predicate neq(Expression<?> val) {
            return notEqual(val);
        }

        public <Y extends Comparable<? super Y>> Predicate gt(Y val) {
            return greaterThan(val);
        }

        public <Y extends Comparable<? super Y>> Predicate gt(Expression<? extends Y> val) {
            return greaterThan(val);
        }

        public <Y extends Comparable<? super Y>> Predicate ge(Y val) {
            return greaterThanOrEqualTo(val);
        }

        public <Y extends Comparable<? super Y>> Predicate ge(Expression<? extends Y> val) {

            return greaterThanOrEqualTo(val);
        }

        public <Y extends Comparable<? super Y>> Predicate lt(Y val) {

            return lessThan(val);
        }

        public <Y extends Comparable<? super Y>> Predicate lt(Expression<? extends Y> val) {
            return lessThan(val);
        }

        public <Y extends Comparable<? super Y>> Predicate le(Y val) {
            return lessThanOrEqualTo(val);
        }

        public <Y extends Comparable<? super Y>> Predicate le(Expression<? extends Y> val) {
            return lessThanOrEqualTo(val);
        }

    }
}

