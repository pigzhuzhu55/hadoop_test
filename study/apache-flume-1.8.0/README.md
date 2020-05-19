<a href="https://github.com/pigzhuzhu55/hadoop_test">返回目录</a>
## 简介
### flume介绍
+ Flume是一个分布式的、高可靠的、高可用的将大批量的不同数据源的日志数据收集、聚合、移动到数据中心（HDFS）进行存储的系统。   
+ 当前Flume有两个版本Flume 0.9X版本的统称Flume-og,Flume1.X版本的统称Flume-ng。两者架构上是不同的。
### flume的组成
+ flume-ng 有3大组件
    * source(源端数据采集)：Flume提供了各种各样的Source、同时还提供了自定义的Source
    * Channel(临时存储聚合数据)：主要用的是memory channel和File channel（生产最常用），生产中channel的数据一定是要监控的，防止sink挂了，撑爆channel
    * Sink（移动数据到目标端）：如HDFS、KAFKA、DB以及自定义的sink

## 安装和使用
### 下载安装包并基础配置
+ 首先，去[官网](http://flume.apache.org/)下载flume安装包，我这里下载的是[1.9.0版本](https://downloads.apache.org/flume/1.9.0/)，解压缩到windows某个目录下
   * 一般配置比较简单，我们拷贝一份模板出来。
    ```shell script
    cp conf/flume-env.sh.template conf/flume-env.sh
    ```
   * 然后修改配置文件`flume-env.sh`的`JAVA_HOME`变量为你的jdk的存放路径。
    ```shell script
    export JAVA_HOME=/usr/local/java/jdk1.8.0_144
    ```
###  使用案例
#### 01、监控文件的目录，运行日志打印在控制台，并最终结果导出到hdfs。
基于工作模板创建自己的属性文件flume1.conf，我把属性文件flume1.conf放在代码里面，加了注释。  
```shell script
cd /usr/cai/apache-flume-1.9.0-bin
./bin/flume-ng agent --conf ./conf/ -f conf/flume1.conf -Dflume.root.logger=DEBUG,console -n agent1
```
上面的agent1就是我配置的flume1.conf文件中定义的agent名称。如果没有出错的话，控制台打印出一些输出成功日志。   
接着我们测试往该监控文件夹里面写入文件，进行监控测试。
```shell script
cd /usr/web/logs
echo 'hello world'>>test1.txt
```
在监控目录中，新建test1.txt文件，并写入`hello world`字符。  
> 我们查看下，输出的结果在hdfs文件系统的flume/event目录下。 

*对了，输出路径也在flume1.conf中配置*
```shell script
cd /usr/cai/hadoop-2.9.2/bin
./hadoop fs -ls /flume/event
```
显示如下
```html
-rw-r--r--   1 root supergroup          5 2020-04-30 20:25 /flume/event/events-.1588249509620
```
查看结果文件的内容
```shell script
./hadoop fs -cat /flume/event/events-.1588249509620
```
显示如下
```html
hello world
```

#### 02、监控一个文件,实时采集新增的数据并输出到控制台
属性文件配置同例一差不多，具体查看flume2.conf  
```shell script
 cd /usr/cai/apache-flume-1.9.0-bin
 ./bin/flume-ng agent --conf ./conf/ -f conf/flume2.conf -Dflume.root.logger=DEBUG,console -n agent2
 ```
模拟往被监控的文件里，写入内容  
```shell script
cd /usr/web/logs
echo 'hello world2'>>test1.txt
echo 'hello world3'>>test1.txt
```
显示如下  
```html
hello world2
hello world3
```

#### 03、通过avro方式对接多个flume，完成多个flume数据的汇总监控
由于多个flume，所以这里得配置不同的属性文件，`flume3_server.conf`、`flume3_clinet.conf`， 具体配置查看代码
*假定flume3_server.conf配置出来的叫flumeServer,flume3_server.conf配置出来的叫flumeClient*   
启动flumeServer，用来接收其他一个或多个flume
```shell script
 cd /usr/cai/apache-flume-1.9.0-bin
 ./bin/flume-ng agent --conf ./conf/ -f conf/flume3_server.conf -Dflume.root.logger=DEBUG,console -n agent3s
 ```

启动flumeClient，监控一个文件,实时采集新增的数据并输出到上面那个flume。我这里只启动一个，而且是单台服务器上模拟。
```shell script
 cd /usr/cai/apache-flume-1.9.0-bin
 ./bin/flume-ng agent --conf ./conf/ -f conf/flume3-client.conf -Dflume.root.logger=DEBUG,console -n agent3c
 ```

模拟往被监控的文件里，写入内容
```shell script
cd /usr/web/logs
echo 'hello world4'>>test1.txt
echo 'hello world5'>>test1.txt
```

这时，flumeClient监控到文件的变化，然后将内容输出给flumeServer,flumeServer控制台显示如下
```html
hello world4
hello world5
```