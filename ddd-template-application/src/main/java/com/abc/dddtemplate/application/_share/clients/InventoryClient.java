package com.abc.dddtemplate.application._share.clients;

import lombok.Builder;
import lombok.Data;

/**
 * 库存客户端
 * @author <template/>
 * @date
 */
public interface InventoryClient {
    Boolean reduce(ReduceParam param);

    @Data
    @Builder
    public static class ReduceParam{
        String productName;
        Integer num;
    }

}
