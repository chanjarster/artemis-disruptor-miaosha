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

### 配置Artemis

下载[Apache Artemis 1.5.4](https://www.apache.org/dyn/closer.cgi?filename=activemq/activemq-artemis/1.5.4/apache-artemis-1.5.4-bin.tar.gz&action=download)。解压之后到任意目录执行以下命令：

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

修改 ${ARTEMIS_INSTANCE}/etc/artemis.profile，将修改这段中-Xms -Xmx的配置，这两个参数控制的是内存：

```bash
JAVA_ARGS="-XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -Xms512M -Xmx1024M"
```

### 配置tomcat

下载[Apache Tomcat 8](http://tomcat.apache.org/download-80.cgi)，解压缩。

修改``${TOMCAT_HOME}/conf/server.xml``文件，替换成以下内容：

```xml
TODO
```

修改``${TOMCAT_HOME}/bin/setenv.sh``文件，内容如下：

```bash
TODO
```

## 启动

### 秒杀后端

新建一个文件``application-backend.properties``，内容如下：
```
TODO
```

运行以下命令启动选课后端程序

```bash
TODO
```


### 秒杀Webapp

新建文件``application-webapp.properties``，内容如下：
```
TODO
```

将``artemis-disruptor-miaosha-web-1.0.0-SNAPSHOT.war``放到``${TOMCAT_HOME}/webapps``下，并改名为``miaosha.war``

到``${TOMCAT_HOME}/conf/Catalina/localhost``下新建``miaosha.xml``文件，内容如下：

```xml
TODO
```
