<a href="https://github.com/pigzhuzhu55/hadoop_test">返回目录</a>

## 简介
### spark介绍
+ Apache Spark是专为大规模数据处理而设计的快速通用的计算引擎。目前已经形成一个高速发展应用广泛的生态系统。
### spark特性
+ 快速
    - 大多数操作均在内存中迭代，只有少部分函数需要落地到磁盘。
+ 易用性
    - 支持scala、Java、Python、R等语言；提供超过80个算子，API使用及其方便。
+ 通用性
    - Spark提供了针对数据处理的一站式解决方案。
        - 计算时用Spark Core算子(替代Hadoop Map/Reduce)
        - 批处理时用Spark SQL(替代HiveSQL)，实时计算用Spark Streaming(替代Stom)，机器学习用
        - Spark MLlib(替代Mahout)；另外，通过Spark GraphX进行图计算。
+ 跨平台运行环境
    - Spark可以使用Local模式，Standalone模式，Cluster模式运行。
        - Local模式：在本地运行调试，支持断点，可以指定并行线程数。
        - Standalone模式：Spark管理资源，有Master和Worker，相当于ResourceManager和NodeManager。
        - Cluster模式：分布式模式，用于生产环境。资源管理器使用Yarn或者Mesos。
### Spark的适用场景
+ 复杂的批处理(Batch Processing)： 偏重点在于处理海量数据的能力,可能是在数十分钟到数小时。Spark RDD,代替了Hadoop MapReduce
+ 实时流处理(Streams Processing)：Spark Streaming和Spark Structured Streaming,通常在数百毫秒到数秒之间,代替了Kafka Streams、Storm
+ 交互式查询（SQL）：Spark SQL,通常的时间在数十秒到数十分钟之间,代替了Hive
+ 机器学习（Machine Learning）： Spark MLLib,代替了Mahout
+ 图形计算（Graph）：Spark Graphx,基于图形存储的NoSQL数据库的计算支持(Neo4J)

### Spark运行模式
+ local 
    - 在本地运行，只有一个工作进程，无并行计算能力。
+ local [K]
    - 在本地运行，有K个工作进程，通常设置K为机器的CPU核心数量。
+ local [*]
    - 在本地运行，工作进程数量等于机器的CPU核心数量。
+ spark://HOST:PORT
    - 以Standalone模式运行，这是Spark自身提供的集群运行模式，默认端口号: 7077
+ mesos://HOST:PORT
    - 在Mesos集群上运行，Driver进程和Worker进程运行在Mesos集群上，部署模式必须使用固定值:--deploy-mode cluster。
+ yarn-client
    - 在Yarn集群上运行，Driver进程在本地，Work进程在Yarn集群上，部署模式必须使用固定值:--deploy-mode client。Yarn集群地址必须在`HADOOP_CONF_DIR` or `YARN_CONF_DIR`变量里定义。
+ yarn-cluster
    - 在Yarn集群上运行，Driver进程在Yarn集群上，Work进程也在Yarn集群上，部署模式必须使用固定值:--deploy-mode client。Yarn集群地址必须在`HADOOP_CONF_DIR` or `YARN_CONF_DIR`变量里定义。


## 安装和使用
### 下载安装包并基础配置
官网介绍: http://spark.apache.org/
历史版本下载: http://archive.apache.org/dist/spark/
> 我这里下载的是spark-2.4.5-bin-without-hadoop.tgz

### 安装先决条件环境
+ java [1.8]
+ scala [2.12.x]
    - 下载地址 https://downloads.lightbend.com/scala/2.12.10/scala-2.12.10.tgz 
+ SSH免密登陆  
+ host主机名  
+ hadoop HDFS环境  
*以上这些环境在其他章节已经介绍过，这里就不累赘*

#### 配置环境变量
```shell script

```
### 安装spark *Standalone模式*
#### 解压文件
把spark安装文件都上传并解压好到linux目录，假定目录为/usr/cai/spark-2.4.5-bin-without-hadoop
#### 修改spark配置文件
+ 打开安装目录的conf目录下
    - 复制配置文件模板,等下分别修改这3个配置文件
        ```shell script
        cd /usr/cai/spark-2.4.5-bin-without-hadoop/conf
        cp slaves.template slaves
        cp spark-env.sh.template spark-env.sh
        cp spark-defaults.conf.template spark-defaults.conf
        ```
    - 修改配置文件**slaves**，这里是伪分布式，所以写入本机IP或者主机名
        ```shell script
        master
        ```
    - 修改配置文件**spark-env.sh**  
      *见本目录中spark-env.sh*
### 启动Spark Standalone模式集群（伪分布式）
> Standalone：类似Hadoop Yarn，是spark自己实现的一个资源调度框架
+ 进入spark的sbin目录 `./start-all.sh` 
> 命令 `jps` 查看服务列表
  如果存在 `Worker` 、`Master` 说明服务启动好了。

## 开发环境搭建
### 使用开发工具创建spark客户端工程
+ 使用idea创建maven工程，下一步下一步
+ 配置POM文件 
+ 配置项目sdk 
    - File --> Project Structure --> Global Libraries -->添加scala sdk目录
+ 添加scala插件
    - File --> Setting --> Plugins --> 选择scala插件安装
+ <a href="../../spark_test/src/main/scala">学习代码</a>

### 常见问题
+ idea调试报：illegal cyclic inheritance involving trait Iterable  
    - 一般是scala的sdk跟spark版本没对上，下载对应的scala版本即可解决*  
    *官网文档每个版本的spark，都有说明当前的支持：Spark runs on Java 8, Python 2.7+/3.4+ and R 3.1+. For the Scala API, Spark 2.4.5 uses Scala 2.12. You will need to use a compatible Scala version (2.12.x).*

+ idea调试下，设置.master("local[*]")程序正常运行；但是设置master("spark://master:7077"),报错：Application has been killed. Reason: All masters are unresponsive! Giving 
    - 目前百度上查了都不好使，我这里换了个安装包部署就好了，本来是用spark-2.4.5-bin-without-hadoop.gz，换成spark-2.4.5-bin-without-hadoop-scala-2.12.gz部署，问题解决。*  

+ 接上，但是新问题又出现，调试时连接到driver被拒绝
    - 看了一下log,是driver是本机，我猜估计是部署方式有关。我暂时没办法在idea中去调试运行Standalone模式*














