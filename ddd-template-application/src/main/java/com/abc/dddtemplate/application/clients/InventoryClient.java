package com.abc.dddtemplate.application.clients;

/**
 * 库存客户端
 * @author <template/>
 * @date
 */
public interface InventoryClient {
    Boolean reduce(String productName, Integer num);
}
