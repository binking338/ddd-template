package com.abc.dddtemplate.convention;

import lombok.Getter;

/**
 * 业务约束
 * @author <template/>
 * @date
 */
public interface Specification<Root> {

    /**
     * 约束是否适用实体
     * @return
     */
    Class<Root> entityClass();

    /**
     * 是否在事务中校验
     * @return
     */
    default boolean inTransaction() {
        return true;
    }

    /**
     * 约束校验
     * @return
     */
    Result valid(Root root);


    /**
     * 规格校验结果
     */
    @Getter
    public static class Result {
        /**
         * 是否通过规格校验
         */
        private boolean passed;
        /**
         * 规格校验反馈消息
         */
        private String message;

        public Result(boolean passed, String message) {
            this.passed = passed;
            this.message = message;
        }

        public static Result pass() {
            return new Result(true, null);
        }

        public static Result fail(String message) {
            return new Result(false, message);
        }
    }
}
