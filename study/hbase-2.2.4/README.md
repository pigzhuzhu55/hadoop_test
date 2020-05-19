<a href="https://github.com/pigzhuzhu55/hadoop_test">返回目录</a>

## 简介
### HBase介绍
+ **Apache HBase**是一个开源的、版本化的、非关系型、构建在**HDFS**上的分布式列存储系统。
+ **HBase**是**Apache Hadoop**生态系统中的重要一员，主要用于海量结构化数据存储。

### Hbase与HDFS对比
+ 两者都具有良好的容错性和扩展性，都可以扩展到成百上千个节点；
    * HDFS适合批处理场景
        - 不支持数据随机查找
        - 不适合增量数据处理
        - 不支持数据更新
    * Hbase表的特点
        - **大**：一个表可以有数十亿行，上百万列；
        - **无模式**：每行都有一个可排序的主键和任意多的列，列可以根据需要动态的增加，同一张表中不同的行可以有截然不同的列；
        - **面向列**：面向列（族）的存储和权限控制，列（族）独立检索；
        - **稀疏**：对于空（null）的列，并不占用存储空间，表可以设计的非常稀疏；
        - **数据多版本**：每个单元中的数据可以有多个版本，默认情况下版本号自动分配，是单元格插入时的时间戳；
        - **数据类型单一**： Hbase中的数据都是字符串，没有类型。
  
## 安装和使用
## 下载安装包并基础配置
官网介绍: http://hbase.apache.org/book.html#_introduction  
历史版本下载: http://archive.apache.org/dist/hbase/
> 因为我用的是hadoop2.9.2，查官网资料，hbase对应支持的版本为2.2.x,所以选择下载最新的hbase_2.2.4吧。
### 解压文件
把Hbase安装文件都上传并解压好到linux目录，假定目录为/usr/cai/hbase-2.2.4
> 因为Hbase有自带的zookeeper，但是分布式生产环境的话一般用自己搭建zookeeper。我下载的是最新版3.6.1，解压目录为/usr/cai/apache-zookeeper-3.6.1-bin
### 增加系统环境变量
使用命令 `vim ~/.bashrc` 编辑文件，增加如下配置：
```shell script
export HBASE_HOME=/usr/cai/hbase-2.2.4
export ZOOKEEPER_HOME=/usr/cai/apache-zookeeper-3.6.1-bin
```
使用命令`source ~/.bashrc`使配置立即生效
### 修改Hbase配置文件
进入安装目录下的**conf**文件夹`cd /usr/cai/hbase-2.2.4/conf`。  
+ 修改配置文件**hbase-env.sh**中的一些变量`vim hbase-env.sh`。
```shell script
export JAVA_HOME=/usr/local/java/jdk1.8.0_144
export HBASE_CLASSPATH=/usr/cai/hadoop-2.9.2/etc/hadoop
export HBASE_MANAGES_ZK=false  
```
> HBASE_MANAGES_ZK=false 表示当前hbase装配为外部的zookeeper组件。如果只是单机玩一玩，这行配置注释掉，默认就是用内部自带的zookeeper。  
+ 修改配置文件**hbase-site.xml**。
```xml
<configuration>
	<property>
		<name>hbase.rootdir</name>
		<value>hdfs://master:9000/hbase</value>
	</property>
	<property>
		<name>hbase.cluster.distributed</name>
		<value>true</value>
	</property>
	<property>
		<name>hbase.zookeeper.quorum</name>
		<value>master,hs01,hs02</value>
	</property>
</configuration>
``` 
+ 修改配置文件**regionservers**,分布式的话，额外添加从节点的IP或者主机名
```shell script
master
hs01
hs02
```

### 分发Hbase配置文件到从节点机器上 
*单机版伪分布式，请去掉配置文件中的hs01,hs02节点，也就没此步骤。*  
上面步骤是在主节点master上配置，现在把做的步骤，在其他节点机器上复制操作一遍。 
> 通过**scp**命令上传，参考**zookeeper**那章节介绍。注意**regionservers**从节点上的配置，添加另外两台主机的**IP**或者主机名。记得如果是主机名要写入**host**文件。 

安装完成。

## HBase启动和停止命令
+ 启动HBase集群：
```shell script
bin/start-hbase.sh
```
+ 单独启动一个HMaster进程：
```shell script
bin/hbase-daemon.sh start master
```
+ 单独停止一个HMaster进程：
```shell script
bin/hbase-daemon.sh stop master
```
+ 单独启动一个HRegionServer进程：
```shell script
bin/hbase-daemon.sh start regionserver
```
+ 单独停止一个HRegionServer进程：
```shell script
bin/hbase-daemon.sh stop regionserver
```

> 注意启动hbase,要先启动htfs,zookeeper。  
> 在浏览器上面查看,输入地址 192.168.237.100:16010 

## 常见问题
+ 我搭建的时候，`hbase`启动后`HMaster`几秒后死掉
```shell script
zookeeper.ClientCnxn: Opening socket connection to server master/127.0.0.1:2181. Will not attempt to authenticate using SASL (unknown error)
```
>也不知道什么原因，可能是我伪分布式下某些参数没有配对，百度到解决办法是   
> 在`hbase-site.xml`中添加如下配置：
```xml
<property>
  <name>hbase.unsafe.stream.capability.enforce</name>
  <value>false</value>
</property>
```
+ 开发程序时候，Java连一下Hbase，一直提示org.apache.hadoop.hbase.MasterNotRunningException: java.net.ConnectException: Call to localhost/127.0.0.1:16000 
> 解决方案 https://blog.51cto.com/12151772/2337703
## 基本用法
### shell
+ `cd /usr/cai/hbase-2.2.4/bin`，进入`shell`命令行
```shell script
./hbase shell
```
> 进入shell模式下，如果敲错字符后想回退，不能直接按backspace ，要按住ctrl,然后backspace

+ 创建一张表
```hbase
create 'hd_product',{NAME => 'name',VERSIONS => 1},{NAME => 'icon',VERSIONS => 3}
## 或者这种写法
create 'hd_product','name','icon'
```
+ 查看表信息
```hbase
describe 'hd_product'
```
+ 查看所有表
```hbase
list
```
+ 删除表要2个步骤，先disable下表，然后再drop
```hbase
disable 'hd_product'
drop 'hd_product'
```
+ 修改表，添加一列族
```hbase
alter 'hd_product',{NAME => 'sname', VERSIONS => 3}
```

+ 修改表，删除一列族
```hbase
alter 'hd_product', 'delete' => 'sname'
```

+ 插入数据 put '表名','行号','列族','列值'
```hbase
put 'hd_product','rowkey_001', 'name:apple','111'
put 'hd_product','rowkey_001', 'icon:apple.jpg','222'
```
+ 删除数据 delete '表名','行号','列族'
```hbase
delete  'hd_product','rowkey_001', 'name:apple'  ##删除某个单元格数据
deleteall   'hd_product','rowkey_001' ##删除某行号数据
```

+ 查看数据 get '表名','行号',{COLUMN=>'列族',VERSIONS=>查看的版本数}
```hbase
scan 'hd_product' ##查看整个表的数据
get 'hd_product','rowkey_001' ##查看某行号数据
get 'hd_product','rowkey_001',{COLUMN=>'name',VERSIONS=>1} ##查看某个列族的数据
```
+ 修改数据 put '表名','行号','列族','新的单元格值' , HBase中实际上是没有修改数据命令的
```hbase
put 'hd_product','rowkey_001', 'name:apple','333'
```

+ 清理表 truncate '表名'
```hbase
truncate 'hd_product'
```

## 开发环境搭建
### 配置windows的hosts文件
+ 配置虚拟机的ip对应的主机名
### 使用开发工具创建hbase客户端工程
+ hbase-site.xml、log4j.properties放到工程里resources目录下
+ 配置POM文件 
+ <a href="../../hbase_test/src/main/java">简单代码</a>