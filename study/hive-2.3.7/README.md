<a href="https://github.com/pigzhuzhu55/hadoop_test">返回目录</a>

## 简介
### Hive介绍
+ Hive 是基于 Hadoop 构建的一套数据仓库分析系统。
+ 它提供了丰富的 SQL 查询方式来分析存储在 Hadoop 分布式文件系统中的数据。
+ 可以将结构化的数据文件映射为一张数据库表，并提供完整的 SQL 查询功能，可以将 SQL 语句转换为 MapReduce 任务进行运行。
+ 通过自己的 SQL 去查询分析需要的内容，这套 SQL 简称 Hive SQL，使不熟悉 MapReduce 的用户很方便地利用 SQL 语言查询、汇总、分析。

### Hive的特点
+ Hive 不适合用于联机(online) 事务处理，也不提供实时查询功能。它最适合应用在基于大量不可变数据的批处理作业。
+ 可伸缩（在Hadoop 的集群上动态的添加设备）。
+ 可扩展、容错、输入格式的松散耦合。
+ Hive 的入口是DRIVER ，执行的 SQL 语句首先提交到 DRIVER 驱动，然后调用 COMPILER 解释驱动， 最终解释成 MapReduce 任务执行，最后将结果返回。
### Hive的数据类型
+ Hive 提供了基本数据类型和复杂数据类型，复杂数据类型是 Java 语言所不具有的。
+ Hive 不支持日期类型，在Hive里日期都是用字符串来表示的，而常用的日期格式转化操作则是通过自定义函数进行操作。
+ Hive 有三种复杂数据类型 ARRAY、MAP 和 STRUCT。ARRAY 和 MAP 与 Java 中的 Array 和 Map 类似，而STRUCT 与 C语言中的 Struct 类似，它封装了一个命名字段集合，复杂数据类型允许任意层次的嵌套。

```mysql
CREATE TABLE complex(
	col1 ARRAY< INT>,
	col2 MAP< STRING,INT>,
	col3 STRUCT< a:STRING,b:INT,c:DOUBLE>
)
```
+ 支持隐式转换    
  *Hive 的原子数据类型是可以进行隐式转换的，类似于 Java 的类型转换，例如某表达式使用 INT 类型，TINYINT 会自动转换为 INT 类型， 但是 Hive 不会进行反向转化，例如，某表达式使用 TINYINT 类型，INT 不会自动转换为 TINYINT 类型，它会返回错误，除非使用 CAST 操作。*
  * 任何整数类型都可以隐式地转换为一个范围更广的类型，如 TINYINT 可以转换成 INT，INT 可以转换成 BIGINT。
  * 所有整数类型、FLOAT 和 String 类型都可以隐式地转换成 DOUBLE。
  * TINYINT、SMALLINT、INT 都可以转换为 FLOAT。
  * BOOLEAN 类型不可以转换为任何其它的类型。

## 安装和使用
### 下载安装包并基础配置
官网介绍: https://cwiki.apache.org/confluence/display/Hive/GettingStarted 
历史版本下载: http://archive.apache.org/dist/hive/
> 我这里下载的是hive-2.3.7
### 解压文件
把hive安装文件都上传并解压好到linux目录，假定目录为/usr/cai/hive-2.3.7
### 增加系统环境变量
使用命令 `vim ~/.bashrc` 编辑文件，增加如下配置：
 ```shell script
 export HIVE_HOME=/usr/cai/apache-hive-2.3.7-bin
 ```
使用命令`source ~/.bashrc`使配置立即生效
### 修改Hive配置文件
进入安装目录下的**conf**文件夹`cd /usr/cai/apache-hive-2.3.7-bin/conf`。  
+ 一般配置比较简单，我们分别拷贝模板出来。
    ```shell script
    cp hive-env.sh.template hive-env.sh
    cp hive-log4j2.properties.template hive-log4j2.properties
    ```
  * 修改配置文件`hive-env.sh`的`JAVA_HOME`、`HIVE_CONF_DIR`、`HIVE_AUX_JARS_PATH`。
    ```shell script
    HADOOP_HOME=/usr/cai/hadoop-2.9.2
    export HIVE_CONF_DIR=/usr/cai/apache-hive-2.3.7-bin/conf
    export HIVE_AUX_JARS_PATH=/usr/cai/apache-hive-2.3.7-bin/lib
    ```
  * 修改配置文件`hive-log4j2.properties`的日志存放路径。*非必须*
    ```shell script
    property.hive.log.dir = /usr/cai/apache-hive-2.3.7-bin/logs
    ```
### 安装hive的元数据库（选择一个即可）
+ 默认元数据库用derby数据库  
  * 实例化metastore命令：
    ```shell script
    cd /usr/cai/apache-hive-2.3.7-bin/bin
    ./schematool -dbType derby -initSchema
    ```shell script  
  * 默认元数据都保存在metastore_db目录里面。
+ 如果元数据库为mysql
  * 修改配置文件hive-site.xml里面的配置信息
    ```shell script
    cp hive-default.xml.template hive-site.xml
    ```
  * 清空hive-site.xml里面的配置信息,添加我们自定义的信息
    ```xml
    <configuration>
         <property>
             <name>javax.jdo.option.ConnectionURL</name>
             <value>jdbc:mysql://master:3306/hive?createDatabaseIfNotExist=true</value>
         </property>
         <property>
             <name>javax.jdo.option.ConnectionDriverName</name>
             <value>com.mysql.jdbc.Driver</value>
         </property>
         <property>
             <name>javax.jdo.option.ConnectionUserName</name>
             <value>root</value>
         </property>
         <property>
             <name>javax.jdo.option.ConnectionPassword</name>
             <value>123456</value>
         </property>
    </configuration>
    ```
  * 拷贝[mysql驱动jar](../sqoop-1.4.7/mysql-connector-java-5.1.31-bin.jar)包到hive安装目录下的lib目录
  * 实例化metastore命令：
    ```shell script
    cd /usr/cai/apache-hive-2.3.7-bin/bin
    ./schematool -dbType mysql -initSchema
    ```shell script  
  * 关于mysql数据库作为元数据库的几点说明
  	 - hive当中创建的表的信息，在元数据库的TBLS表里面  
  	 - 这个表的字段信息，在元数据库的COLUMNS_V2表里面
  	 - 这个表在HDFS上面的位置信息，在元数据库的SDS表里面
> 注意启动hive,要先启动htfs 

## 基本用法
### 进入命令行模式
    ```shell script
    cd /usr/cai/apache-hive-2.3.7-bin/bin 
	./hive
    ```shell script 
+ 查看所有表
```hiveql
show tables ;
```
+ 创建表
    * 1、创建一张简单表
    ```hiveql
    create table people (id int, name string) 
    row format delimited 
    fields terminated by '\t';
    ```
    > 我们可以网页http://192.168.237.100:50070/【ip为自己hodoop主机的ip】去查看一些信息*   
    
    *比如hdfs目录下/user/hive/warehouse，可以看到刚才创建的那张表people。*
    
    + 加载文本数据到表
    准备文本数据，如people.txt
    ```text
    1   xiaoming
    2   xiaohua
    3   莉莉
    4   虎虎
    ```
    ```hiveql
    load data local inpath '/usr/cai/people.txt' into table people;
    ```
    *同样通过浏览器查看，hdfs目录下/user/hive/warehouse/people，可以看到刚才加载的文件。*
    * 2、创建一张复杂一点的表
    ```hiveql
    create table person(
    id int,
    name string,
    hobby array<string>,
    addr map<string,string>
    )
    row format  delimited 
    fields terminated by ','
    collection items terminated by '-'
    map keys terminated by ':'
    ;
    ```
    准备文本数据，如person.txt
    ```text
    1,xiaoming,book-tv-code,beijing:shanghai
    2,xiaohua,tv-swimming,guangdong:shanghai
    3,莉莉,swimming,xiamen
    4,虎虎,basketball-tv-code,beijing
    ```
    ```hiveql
    load data local inpath '/usr/cai/person.txt' into table person;
    ```
    * 3、创建一张复杂一点的外部表
    ```hiveql
    create external table person2(
    id int,
    name string,
    hobby array<string>,
    addr map<string,string>
    )
    row format  delimited 
    fields terminated by ','
    collection items terminated by '-'
    map keys terminated by ':'
    ;
    ```
    ```hiveql
    load data local inpath '/usr/cai/person.txt' into table person2;
    ```
    * 前面2张未指明`external`是内部表，后面1张是外部表
      - 内部表数据由Hive自身管理，外部表数据由HDFS管理； 
      - 内部表数据存储的位置是hive.metastore.warehouse.dir（默认：/user/hive/warehouse）
      - 外部表数据的存储位置由自己制定（如果没有LOCATION，Hive将在HDFS上的/user/hive/warehouse文件夹下以外部表的表名创建一个文件夹，并将属于这个表的数据存放在这里）
      - 删除内部表会直接删除元数据（metadata）及存储数据；删除外部表仅仅会删除元数据，HDFS上的文件并不会被删除；
      
    * 4、创建一张复杂一点的分区表
    ```hiveql
    create external table person3(
    id int,
    name string,
    hobby array<string>,
    addr map<string,string>
    )
    partitioned by (pt string)
    row format  delimited 
    fields terminated by ','
    collection items terminated by '-'
    map keys terminated by ':'
    ;
    ```
    ```hiveql
    load data local inpath '/usr/cai/person.txt' into table person3 partition (pt='202005');
    ```
+ 查询表
```hiveql
select * from people order by id desc;
```
结果
```text
Total MapReduce CPU Time Spent: 5 seconds 600 msec
OK
4	虎虎
3	莉莉
2	xiaohua
1	xiaoming
Time taken: 41.963 seconds, Fetched: 4 row(s)
```
+ 查询所有数据库
```hiveql
show databases
```
+ 创建数据库
```hiveql
create database userdb;
```
+ 删除数据库
*默认情况下，hive不允许删除含有表的数据库*   
`restrict`：默认值，要先删除表才能删除数据库。   
`cascade`：强制删除一个数据库   
```hiveql
drop database if exists userdb restrict;
```
+ 其他
```hiveql
alter table people3 add partition (pt='202005');
alter table people3 drop partition (pt='202005');
show partitions people3;
```

*一篇文章让你了解Hive和HBase的区别 https://blog.csdn.net/wshyb0314/article/details/81475475*