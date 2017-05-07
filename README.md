# artemis-disruptor-miaosha

基于Apache Artemis 和 Disruptor 的秒杀项目demo。

本项目的灵感来自于[秒杀、抢购解决方案，设计目标：性能支撑"小米印度抢购搞挂亚马逊事件”](http://git.oschina.net/1028125449/miaosha)。

在看了很多基于redis+消息队列的秒杀架构之后，决定自己写一点特别的秒杀架构。

## 架构说明

从部署拓扑上看，架构分为4个部分：

1. 运行在Tomcat中的webapp
2. Artemis消息队列
3. standalone backend程序
4. Oracle数据库

## 性能表现

先说一下性能表现吧，因为大家对这个比较感兴趣。

硬件环境（所有程序都是在一台电脑上做的）：

* MacBook Pro (Retina, 15-inch, Mid 2014)
* 2.2 GHz Intel Core i7
* 16 GB 1600 MHz DDR3
* 512G SSD

软件环境：

* java version "1.8.0_131"
* Artemis 1.5.4
* Oracle XE 11g (docker)
* Tomcat 8.5.14

相关配置见[如何准备环境](Environment.md)

一共Benchmark了两次，因为在测试过程中发现Tomcat在warm-up之后性能会更好，两次都是30W请求，测试Jmeter脚本见[如何Benchmark](Benchmark.md)。

第一次结果：

* Tomcat的表现：300000 in 00:01:57 = 2569.8/s Avg:   108 Min:     0 Max: 41102 Err:   164 (0.05%)
* 数据库表现：299836条订单 ／ 121秒 = 2477条/s

PS. 数据库表现从后端程序的日志中分析的。

第二次结果：

不重启Tomcat和Artemis，把数据库的数据恢复后，重启了后端程序

* Tomcat的表现：300000 in 00:00:35 = 8527.8/s Avg:    20 Min:     0 Max:  4515 Err:     2 (0.00%)
* 数据库表现：246873 / 46 秒 = 5366条 / s

数据库记录数偏少是因为Artemis缓存区满了，把消息丢掉了

```
11:47:14,789 WARN  [org.apache.activemq.artemis.core.server] AMQ222039: Messages sent to address 'jms.queue.MiaoSha.request' are being dropped; size is currently: 104,858,376 bytes; max-size-byt
```

## 优化项

架构上的优化点

1. 下单请求异步处理
1. 在秒杀期间，商品库存信息在内存中，库存判断及库存扣减都在内存中进行，之后异步到数据库
1. 利用Disruptor将并发请求串行化，同时避免了多线程编程复杂度
1. 抛弃数据库事务，采用最终一致性

和JMS相关的优化点

1. 重用JMS Connection、Session、MessageProducer、MessageConsumer，而不是每次都创建这些对象（Spring的JmsTemplate就是这么干的）
1. 将JMS Session设定为transacted=false, AUTO_ACKNOWLEDGE
1. 发送JMS消息是，DeliveryMode=NON_PERSISTENT
1. 关闭Artemis的重发、消息持久机制

和JDBC相关的优化点

1. 使用JDBC Batch Update，减少和数据库网络IO的次数
1. 优化更新商品库存的DB操作，将多个更新商品库存的请求合并成一条update，而不是多个update

## 流程说明

本项目只提供了两个接口：

1. 下单接口。用于下单。
2. 查询下单结果的接口。用于查询下单是否成功。

聪明的读者肯定已经想到了，整个秒杀过程是异步的。

### 下单流程

![下单流程](design/UML/流程图-下单.png)

### 查询下单结果的流程

![查询下单结果流程](design/UML/流程图-查询下单结果.png)  


## How-tos

* [如何准备环境](Environment.md)
* [如何构建](Build.md)
* [如何启动](Run.md)
* [如何访问](Test.md)
* [如何Benchmark](Benchmark.md)
