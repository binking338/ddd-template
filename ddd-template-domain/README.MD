# domain项目说明

## aggregates目录
聚合，核心业务逻辑

## events目录
领域事件，定义本上下文能够产生的领域事件

## services目录
领域服务，用于实现多个聚合或者与外部系统实体协作才能完成的业务。
领域服务必须是无状态的【important!】。

## specifications目录
约束逻辑