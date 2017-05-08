# benchmark

先执行以下sql添加商品数据

```sql
insert into ITEM(ID, NAME, AMOUNT) VALUES (1, '商品01', 300000);
```

到[这里](http://jmeter.apache.org/download_jmeter.cgi)下载Jmeter 3.x版本。

用Jmeter打开项目``jmeter/benchmark.jmx``文件。

1. 修改**Thread Group**里的用户数到你期望的数值
2. 修改**Aggregate Graph**里的结果保存路径
3. 修改**View Results Tree**里的结果保存路径

关闭Jmeter，利用以下命令使用Jmeter的Non-GUI模式跑测试：

```bash
JVM_ARGS="-Xms1g -Xmx1g" ${JMETER_HOME}/bin/jmeter.sh -n -t jmeter/benchmark.jmx的绝对路径
```

测试完毕后，用Jmeter打开``jmeter/benchmark.jmx``文件，分别到

* **Aggregate Graph**
* **View Results Tree**

里加载结果文件，查看结果
