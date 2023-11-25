package com.abc.dddtemplate.application.clients;

import lombok.Builder;
import lombok.Getter;

/**
 * 定义外部服务的访问接口（风格一，推荐）
 *
 * 应用场景
 * 【命令】接口属于查询类功能，用于查询前置约束判断需要的相关信息；接口属于命令类功能，分布式事务场景（不推荐）
 * 【查询】响应结果部分字段来源于其他的依赖服务
 * @author <template/>
 * @date
 */
@Getter
@Builder
public class SysTime {
    /**
     * 数据信息
     */
    String data;

    /**
     * 客户端接口（简单示意，实际条件应该是复杂的，复杂参数结构可以定义到dto包中）
     * 在adapter中实现
     */
    public interface Client {
        SysTime get();
    }
}
