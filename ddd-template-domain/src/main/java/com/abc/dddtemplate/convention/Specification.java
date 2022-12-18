package com.abc.dddtemplate.convention;

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
    boolean inTransaction();

    /**
     * 约束校验
     * @return
     */
    boolean valid(Root root);

    /**
     * 约束校验失败提示
     * @param root
     * @return
     */
    String failMsg(Root root);
}
