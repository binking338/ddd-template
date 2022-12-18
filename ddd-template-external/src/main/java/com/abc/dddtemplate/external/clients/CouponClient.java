package com.abc.dddtemplate.external.clients;

/**
 * 优惠券客户端
 * @author <template/>
 * @date
 */
public interface CouponClient {
    /**
     * 优惠券扣减
     * @param name
     * @param amount
     * @return
     */
    Boolean deduct(String name, Integer amount);
}
