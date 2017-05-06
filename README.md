# artemis-disruptor-miaosha

基于Apache Artemis 和 Disruptor 的秒杀项目demo

## 编译打包
1. 到[这里](http://www.oracle.com/technetwork/database/features/jdbc/jdbc-drivers-12c-download-1958347.html)下载ojdbc7.jar，然后运行以下命令将其安装到到本地maven仓库
```bash
mvn install:install-file -Dfile=ojdbc7.jar -DgroupId=com.oracle -DartifactId=ojdbc -Dversion=12.1.0.2 -Dpackaging=jar
```
2. 运行``maven clean install``命令打包整个项目
3. 到``web/target/artemis-disruptor-miaosha-web-1.0.0-SNAPSHOT.war``获得webapp的war包
4. 到``backend/target/artemis-disruptor-miaosha-backend-1.0.0-SNAPSHOT.jar``获得后端程序的jar包

## 准备环境

### 安装JDK8

下载[JDK8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

### 安装Oracle

如果有条件，可以使用Oracle 12c版本，如果没有则可以使用Oracle 11g。

本人用的是[Oracle EX 11g的Docker image](https://hub.docker.com/r/wnameless/oracle-xe-11g/)，下面介绍Docker的流程


```bash
# docker下载oracle-xe-11g的image，并启动
docker run -d -p 1521:1521 --name oralce-xe wnameless/oracle-xe-11g

# 关闭oracle-xe
docker stop oracle-xe

# 启动oracle-xe
docker start oracle-xe
```

用任意客户端连接oracle，推荐使用Intellij IDEA自带的Database工具连接，这样就不用额外下载客户端了。

以用户名system，密码oracle连接oracle，执行以下sql创建schema：

```sql
CREATE USER miaosha IDENTIFIED BY "miaosha";
```

然后以用户名miaosha，密码miaosha连接oracle，执行以下sql初始化数据：

```sql
CREATE TABLE ITEM (
  ID     NUMBER(19) PRIMARY KEY,
  NAME   VARCHAR2(500),
  AMOUNT NUMBER(19) DEFAULT 0 NOT NULL
);
CREATE SEQUENCE SEQ_ITEM;
CREATE TABLE ITEM_ORDER (
  ID      NUMBER(19) PRIMARY KEY,
  ITEM_ID NUMBER(19) REFERENCES ITEM (ID) NOT NULL,
  USER_ID VARCHAR(500)
);
CREATE SEQUENCE SEQ_ITEM_ORDER CACHE 1000;

insert into ITEM(ID, NAME, AMOUNT) VALUES (1, '商品01', 300000);
commit;
```

### 配置Artemis

下载[Apache Artemis 1.5.4](https://www.apache.org/dyn/closer.cgi?filename=activemq/activemq-artemis/1.5.4/apache-artemis-1.5.4-bin.tar.gz&action=download)，解压。

到任意目录执行以下命令：

```bash
${ARTEMIS_HOME}/bin/artemis create \
        --user miaosha \
        --password miaosha \
        --role client \
        --require-login \
        --disable-persistence \
        --topics MiaoSha.response \
        --queues MiaoSha.request \
        --no-stomp-acceptor \
        --no-mqtt-acceptor \
        --no-amqp-acceptor \
        --no-hornetq-acceptor \
        --no-web \
        -- miaosha-broker
```

把命令里的${ARTEMIS_HOME}替换成为Apache Artemis的解压目录。

命令执行完毕后会产生一个``miaosha-broker``的目录，进入这个目录，修改``etc/broker.xml``文件，修改``<address-settings>``部分，变成这样

```xml
      <address-settings>
         <address-setting match="jms.*.MiaoSha.#">
            <redelivery-delay>0</redelivery-delay>
            <max-delivery-attempts>0</max-delivery-attempts>
            <max-size-bytes>52428800</max-size-bytes>
            <message-counter-history-day-limit>1</message-counter-history-day-limit>
            <address-full-policy>DROP</address-full-policy>
         </address-setting>
  
         <!--default for catch all
         <address-setting match="#">
            <dead-letter-address>jms.queue.DLQ</dead-letter-address>
            <expiry-address>jms.queue.ExpiryQueue</expiry-address>
            <redelivery-delay>0</redelivery-delay>
            <max-size-bytes>10485760</max-size-bytes>
            <message-counter-history-day-limit>10</message-counter-history-day-limit>
            <address-full-policy>BLOCK</address-full-policy>
         </address-setting>
         -->
      </address-settings>
```

修改``miaosha-broker/etc/artemis.profile``，修改这段中``-Xms2g -Xmx2g``的配置，这两个参数控制的是内存：

```bash
JAVA_ARGS="-XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -Xms2g -Xmx2g"
```

### 配置tomcat

下载[Apache Tomcat 8](http://tomcat.apache.org/download-80.cgi)，解压缩。

修改``${TOMCAT_HOME}/conf/server.xml``文件，替换成以下内容：

```xml
<Server port="8005" shutdown="SHUTDOWN">
  <Listener className="org.apache.catalina.startup.VersionLoggerListener" />
  <Listener className="org.apache.catalina.core.AprLifecycleListener" SSLEngine="on" />
  <Listener className="org.apache.catalina.core.JreMemoryLeakPreventionListener" />
  <Listener className="org.apache.catalina.mbeans.GlobalResourcesLifecycleListener" />
  <Listener className="org.apache.catalina.core.ThreadLocalLeakPreventionListener" />

  <Service name="Catalina">

    <Connector port="8080" protocol="HTTP/1.1"
      URIEncoding="UTF-8"
      enableLookups="false"
      acceptCount="100"
      maxThreads="1000"
      minSpareThreads="200"
      connectionTimeout="15000"      
    />

    <Engine name="Catalina" defaultHost="localhost">
      <Host name="localhost"  appBase="webapps" unpackWARs="true" autoDeploy="true">
      </Host>
    </Engine>
  </Service>

</Server>
```

新建``${TOMCAT_HOME}/bin/setenv.sh``文件，内容如下：

```bash
CATALINA_OPTS="-server -Xmx2G -Xms2G"
```

## 启动

### Apache Artemis

```bash
miaosha-broker/bin/artemis-service start
```

### 秒杀Webapp

新建文件``application-jms-client.properties``，内容见这里[application-jms-client.properties](jms-client/src/main/resources/application-jms-client.properties)

将``artemis-disruptor-miaosha-web-1.0.0-SNAPSHOT.war``放到``${TOMCAT_HOME}/webapps``下，并改名为``miaosha.war``

到``${TOMCAT_HOME}/conf/Catalina/localhost``下新建``miaosha.xml``文件，内容如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Context>
  <Environment name="spring.config.location" value="application-webapp.properties的绝对路径" type="java.lang.String"/>
  <Resources cachingAllowed="true" cacheMaxSize="100000" />
</Context>
```

PS. webapp可以有多个节点，利用haproxy、nginx做反向代理。

### 秒杀后端

新建一个文件``application-jms-server.properties``，内容见这里[application-jms-server.properties](jms-server/src/main/resources/application-jms-server.properties)。需要注意的是，要修改``spring.datasource.url``参数到你自己的数据库上。

运行以下命令启动选课后端程序

```bash
java -jar -server -Xms2g -Xmx2g \
  -Dspring.config.location=application-jms-server.properties的路径 \
  artemis-disruptor-miaosha-backend-1.0.0-SNAPSHOT.jar
```

PS. 秒杀后端只能部署有一个节点，因为商品的库存数据都在内存，而这些数据是不跨jvm共享的。

## 访问

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

## 利用Jmeter benchmark

到[这里](http://jmeter.apache.org/download_jmeter.cgi)下载Jmeter 3.x版本。

用jmeter打开项目``jmeter/benchmark.jmx``文件。

1. 修改**Thread Group**里的用户数到你期望的数值
2. 修改**Aggregate Graph**里的结果保存路径

关闭jmeter，利用以下命令使用jmeter的Non-GUI模式跑测试：

```bash
JVM_ARGS="-Xms1g -Xmx1g" ${JMETER_HOME}/bin/jmeter.sh -n -t jmeter/benchmark.jmx的绝对路径
```

