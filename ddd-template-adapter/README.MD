# adapter项目说明
external、domain、application项目中声明的接口实现在这个项目中。

## application/api目录
定义和实现 http api 接口

## application/jobs目录
实现 xxljob 框架的任务处理程序

## application/queues目录
实现 异步任务队列 的消费逻辑

## domain/events目录
实现 外部服务领域事件 的消费逻辑

## domain/repositories目录
实现 聚合仓储 接口

## external/clients目录
实现 外部服务接口 适配

## external/reports目录
实现 复杂查询 逻辑

