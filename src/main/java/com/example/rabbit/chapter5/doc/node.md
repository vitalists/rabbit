# 防止消息丢失:

## 1.持久化:
### 持久化包括 交换机持久化、队列持久化、消息持久化
- 交换机持久化:交换机声明时将durable 参数设为true
- 队列持久化: 队列声明时将durable参数设为true
- 消息持久化:发送消息时将投递模式设为2即BasicProperties中的deliveryMode属性设为2即可
### 要点:如果需要消息持久化需要将交换机、队列、消息同时持久化.

## 2.生产者确认
### 如果生产者发出去的消息没有得到队列的确认那么也会造成消息丢失
#### 解决生产者发送的消息丢失的措施:
- 通过事务机制实现
- 通过发送方消息确认机制实现(publisher confirm)
##### 事务机制:
- channel.txSelect 将当前信道设置为事务模式
- channel.txCommit 提交事务
- channel.RollBack 回滚事务
```java
        AMQP.Tx.SelectOk selectOk = channel.txSelect();
        AMQP.Tx.CommitOk commitOk = channel.txCommit();
        AMQP.Tx.RollbackOk rollbackOk = channel.txRollback();
```
try:{
    // 开启事务
    channel.txSelect() // 服务器端会回复Tx.select-Ok状态表示开启事务
    // 发送消息
    channel.basicPublic() 
    // 提交事务
    channel.txCommit  //Broker 会回复Tx.Commit.OK 表示事务提交成功
}catch(***Exception e){
    // 异常回滚事务
    channel.txRollBack()
}

#### 可以通过Broker返回的状态监听事务是否提交正常.
#### 开启事务会导致性能降低,所以引入了轻量级的消息确认机制
- 1.开启confirm模式.
- 2.发送消息
- 3.监听消息确认



