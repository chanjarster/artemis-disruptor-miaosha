# 准备环境

## 安装JDK8

下载[JDK8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

## 安装Oracle

如果有条件，可以使用Oracle 12c版本，如果没有则可以使用Oracle 11g。

本人用的是[Oracle EX 11g的Docker image](https://hub.docker.com/r/wnameless/oracle-xe-11g/)，下面介绍Docker的流程：

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

CREATE TABLE ITEM_ORDER (
  ID      NUMBER(19) PRIMARY KEY,
  ITEM_ID NUMBER(19) REFERENCES ITEM (ID) NOT NULL,
  USER_ID VARCHAR(500)
);
CREATE SEQUENCE SEQ_ITEM_ORDER CACHE 1000;
```

## 配置ActiveMQ Artemis

下载[ActiveMQ Artemis 1.5.5](http://activemq.apache.org/artemis/download.html)，解压。

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

## 配置Tomcat

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

    <Connector port="8080" protocol="org.apache.coyote.http11.Http11Nio2Protocol"
      URIEncoding="UTF-8"
      enableLookups="false"

      maxThreads="500"
      minSpareThreads="500"
      processorCache="500"
      
      acceptCount="5000"
      maxConnections="10000"

      connectionTimeout="15000"  
    />

    <Engine name="Catalina" defaultHost="localhost">
      <Host name="localhost"  appBase="webapps" unpackWARs="true" autoDeploy="true">
      </Host>
    </Engine>
  </Service>

</Server>
```

上面关于各个参数的说明见[这里](http://tomcat.apache.org/tomcat-8.5-doc/config/http.html)

新建``${TOMCAT_HOME}/bin/setenv.sh``文件，内容如下：

```bash
CATALINA_OPTS="-server -Xmx4G -Xms4G"
```
