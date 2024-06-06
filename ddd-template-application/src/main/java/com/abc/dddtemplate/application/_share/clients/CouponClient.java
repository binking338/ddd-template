package com.abc.dddtemplate.application._share.clients;

import lombok.Builder;
import lombok.Data;

/**
 * 优惠券客户端
 * @author <template/>
 * @date
 */
public interface CouponClient {
    /**
     * 优惠券扣减
     * @param param
     * @return
     */
    Boolean deduct(DeductParam param);

    @Data
    @Builder
    public static class DeductParam {
        String name;
        Integer amount;
    }
}
