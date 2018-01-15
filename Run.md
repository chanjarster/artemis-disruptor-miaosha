# 启动

## ActiveMQ Artemis

```bash
miaosha-broker/bin/artemis-service start
```

## webapp

新建文件``application-jms-client.properties``，内容见这里[application-jms-client.properties](jms-client/src/main/resources/application-jms-client.properties)

将``artemis-disruptor-miaosha-web-1.1.0-SNAPSHOT.war``放到``${TOMCAT_HOME}/webapps``下，并改名为``miaosha.war``

到``${TOMCAT_HOME}/conf/Catalina/localhost``下新建``miaosha.xml``文件，内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
  <Environment name="spring.config.location" value="application-jms-client.properties的绝对路径" type="java.lang.String"/>
  <Resources cachingAllowed="true" cacheMaxSize="100000" />
</Context>
```

PS. webapp可以有多个节点，利用haproxy、nginx做反向代理。

## backend

新建一个文件``application-jms-server.properties``，内容见这里[application-jms-server.properties](jms-server/src/main/resources/application-jms-server.properties)。需要注意的是，要修改``spring.datasource.url``参数到你自己的数据库上。

运行以下命令启动选课后端程序

```bash
java -jar -server -Xms2g -Xmx2g \
  -Dspring.config.location=application-jms-server.properties的路径 \
  artemis-disruptor-miaosha-backend-1.1.0-SNAPSHOT.jar
```

PS. 秒杀后端只能部署有一个节点，因为商品的库存数据都在内存，而这些数据是不跨JVM共享的。

## 测试

下单：

```bash
curl -X POST 'http://localhost:8080/miaosha/order' --data 'itemId=1'
```

返回结果：

```bash
{"id":"325e5ef0-322a-11e7-a867-b20ed8864300","itemId":1,"userId":"HFxUS"}
```

上面返回的JSON的ID就是下单请求ID，然后利用这个请求ID获得下单结果：

```bash
curl -X GET 'http://localhost:8080/miaosha/order-result?requestId=325e5ef0-322a-11e7-a867-b20ed8864300'
```

返回结果：

```bash
{"id":"dc6abcb6-3229-11e7-9067-b20ed8864300","requestId":"325e5ef0-322a-11e7-a867-b20ed8864300","errorMessage":null,"success":true}
```
