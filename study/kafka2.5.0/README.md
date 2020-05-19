<a href="https://github.com/pigzhuzhu55/hadoop_test">返回目录</a>

## 简介
### Kafka介绍
+ Kafka是最初由Linkedin公司开发，是一个分布式、支持分区的（partition）、多副本的（replica），基于zookeeper协调的分布式消息系统。
+ 支持Kafka Server间的消息分区，及分布式消费，同时支持离线数据处理和实时数据处理。
+ 高吞吐率，支持在线水平扩展。
+ 用scala语言编写，Linkedin于2010年贡献给了Apache基金会并成为顶级开源项目。
### kafka基本名词解释
+ Broker          消息中间件处理节点，一个Kafka节点就是一个broker，一个或者多个Broker可以组成一个Kafka集群
+ Topic           Kafka根据topic对消息进行归类，发布到Kafka集群的每条消息都需要指定一个topic
+ Producer        消息生产者，向Broker发送消息的客户端
+ Consumer        消息消费者，从Broker读取消息的客户端
+ ConsumerGroup   每个Consumer属于一个特定的Consumer Group，一条消息可以发送到多个不同的Consumer Group，但是一个Consumer Group中只能有一个Consumer能够消费该消息
+ Partition       物理上的概念，一个topic可以分为多个partition，每个partition内部是有序的
### Kafka的消息传输保障
+ At most once: 消息可能会丢，但绝不会重复传输
+ At least once：消息绝不会丢，但可能会重复传输
+ Exactly once：每条消息肯定会被传输一次且仅传输一次

## 安装和使用
### 下载安装包并基础配置
官网介绍: http://kafka.apache.org
历史版本下载: http://archive.apache.org/dist/kafka/
> 我这里下载的是kafka-2.5.0(Scala 2.13)
### 解压文件
把kafka安装文件都上传并解压好到linux目录，假定目录为/usr/cai/kafka_2.13-2.5.0
### 修改kafka配置文件
+ 编辑server.properties文件，修改zk的服务地址，比如：
	zookeeper.connect=master:2181
*zk集群则多个ip按,隔开，比如zookeeper.connect=master:2181,salve1:2181,salve2:2181*

### 启动zookeeper集群
[见Hbase章节](../hbase-2.2.4) 
### 启动kafka服务
```shell script
cd /usr/cai/kafka_2.13-2.5.0/bin
./kafka-server-start.sh ../config/server.properties 
``` 
### 命令模式练习
+ 新启动克隆一个会话来进行，然后创建一个kafka主题Topic
```shell script
cd /usr/cai/kafka_2.13-2.5.0/bin
./kafka-topics.sh --create --zookeeper master:2181 --replication-factor 1 --partitions 1 --topic mykaka
``` 
控制台显示如下：
```shell script
Created topic mykaka.
``` 
+ 查看有那些topic：
```shell script
./kafka-topics.sh --list --zookeeper master:2181
``` 
控制台显示如下：
```shell script
mykaka
``` 
+ 创建一个生产者，这里是使用命令行的producer
```shell script
./kafka-console-producer.sh --broker-list localhost:9092 --topic mykaka
``` 
+ 新启动克隆一个会话来进行，创建一个消费者，这里也是用命令行的consumer
```shell script
cd /usr/cai/kafka_2.13-2.5.0/bin
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic mykaka --from-beginning
``` 

> 接着上面几部操作完成后，在producer那端会话窗口敲入字符，在consumer那端会话窗口就能接收到字符。












