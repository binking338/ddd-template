package com.abc.dddtemplate.application._share.clients;

import lombok.Builder;
import lombok.Data;

/**
 * 定义外部服务的访问接口（风格二）
 *
 * 应用场景
 * 【命令】接口属于查询类功能，用于查询前置约束判断需要的相关信息；接口属于命令类功能，分布式事务场景（不推荐）
 * 【查询】响应结果部分字段来源于其他的依赖服务
 * @author <template/>
 * @date
 */
public interface TimeServiceClient {

    SysTimeDto getSysTime();

    /**
     * 防腐模型，应用于external项目的clients接口
     * @author <template/>
     * @date
     */
    @Data
    @Builder
    class SysTimeDto {
        String timestamp;
    }
}
