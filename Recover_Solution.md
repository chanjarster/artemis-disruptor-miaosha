# 崩溃恢复方案

这个架构里涉及到3个节点：Webapp、Artemis、Backend，当节点意外崩溃的时候，我们就需要恢复，有些时候还需要恢复数据，这里讲一下恢复方案。

## Webapp崩溃恢复

Webapp本身并不保存状态数据，它唯一保存是ResultCache，而ResultCache丢失是没有什么问题的。所以当Webapp崩溃的时候，只需要重启就行了。

## Artemis崩溃恢复

Artemis是一个消息中间件，分两个场景来看问题。

场景一：Webapp将Request发送给它，它再发送到Backend，如果它崩溃了，那就存在三种可能性：

1. Webapp -> Artemis 失败
1. Webapp -> Artemis 成功，Artemis -> Backend 失败
1. Webapp -> Artemis 成功，Artemis -> Backend 成功

前两种我们可以认为Request并没有发送到后端，这个时候什么事情都没有发生，所以Request丢失无所谓。第三种我们认为Request发送成功了，并没有什么特别需要处理的。

场景二：Backend将Result发送给Artemis，Artemis再发送到Webapp，如果它崩溃了，那同样存在三种可能性：

1. Backend -> Artemis 失败
1. Backend -> Artemis 成功，Artemis -> Webapp 失败
1. Backend -> Artemis 成功，Artemis -> Webapp 成功

用户查看订单（这个才代表是否真的下单成功）的地方不在这里，Result只是一个结果状态，所以丢失Result是可以接受的。

综合上面两个场景，结论就是如果Artemis崩溃，只需要重启就行了。

## Backend崩溃恢复

Backend负责处理业务逻辑（判断是否可以下单）以及将结果入库，如果它崩溃了很可能会发生数据丢失的情况，因为我们这里采用的是最终一致性，没有采用事务，所以丢失数据的恢复需要借助日志才可以。

先来看一下Backend处理Request的大致步骤：

1. crash point 1 接收到Request
1. crash point 2 业务逻辑判断
1. crash point 3 准备入库
1. crash point 4 执行入库操作
1. crash point 5 入库结束

下面来讲解各个crash point的处理方式：

1. crash point 1，这时崩溃无所谓，Request丢失可接受
1. crash point 2，这时崩溃无所谓，Request丢失可接受
1. crash point 3，这时崩溃会产生数据丢失
1. crash point 4，这时崩溃会产生数据丢失
1. crash point 5，这时崩溃无所谓，因为数据都已经入库了
 
从上面可以看出，真正要关心的是在3、4步骤时发生的崩溃，我们要针对这个建立一套恢复机制，其实这套机制比较简单，总结下来就是这么几步：

1. 在准备入库前，将一套Command（数据库操作）写到日志文件里，之所以是一套Command是因为一个秒杀入库包含了多个Command。
1. 在入库结束后，将Command执行结果写到日志文件里，这里不是一套Command，因为我们的Command是并行执行的。

当发生崩溃后，我们开启恢复模式，顺序读取日志文件，找出没有执行或执行失败的Command，将其补充执行就行了。

> 注意：此处仅提供设计思路，本项目中并未实现。

下面是日志文件的大致格式：

```
{header} : [EMIT]A={Json}/DELIMITER/B={Json}/EOL/
{header} : [OPTM]A={Json}/EOL/
{header} : [EXEC]A={Json}/EOL/
{header} : [FAIL]A={Json}/EOL/

{header}: 日志行的头，一般都是基础信息，比如时间戳、PID、线程什么的
/DELIMITER/: 分隔符
/EOL/: 行终结符
[EMIT]: 发射
[OPTM]: 被优化，命令被优化是因为两个命令针对同一个Entity，那么只需要执行后面一个，前面一个不需要被执行
[EXEC]: 执行成功
[FAIL]: 执行失败
A,B: Command名称
{Json}: Command Json字符串
```

要特别注意的是，每个Command都应该有一个**业务主键**，比如我们这里的商品ID就是业务主键。
**业务主键**之所以重要是因为当针对某个商品ID的Command存在多个时，其中有一个失败或丢失，并不一定需要恢复那个Command。以[ItemAmountUpdateCommand][src-ItemAmountUpdateCommand]举例：

1. [EMIT]: {id: "abcdef", itemId:1, amount=100}/DELIMITER/{其他Command}
1. [FAIL]: {id: "abcdef", itemId:1, amount=100}
1. [EMIT]: {id: "xyzabc", itemId:1, amount=90}/DELIMITER/{其他Command}
1. [EXEC]: {id: "xyzabc", itemId:1, amount=90}

上面这个例子，Command[abcdef]执行失败了，但是后面有一个Command[xyzabc]针对同一个itemId执行成功了，也就是说将Command[abcdef]的失败补偿掉了。

  [src-ItemAmountUpdateCommand]: jms-server/src/main/java/me/chanjar/jms/server/command/item/ItemAmountUpdateCommand.java
