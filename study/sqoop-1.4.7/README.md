<a href="https://github.com/pigzhuzhu55/hadoop_test">返回目录</a>
## 简介
### Sqoop介绍
Sqoop是一个用于在Hadoop和关系数据库服务器之间传输数据的工具。    
+ 把关系型数据库的数据导入到 `Hadoop` 系统 ( 如 `HDFS`、`HBase` 和 `Hive`) 中；  
+ 把数据从 `Hadoop` 系统里抽取并导出到关系型数据库里。  

## 准备安装包
首先，去[官网](http://sqoop.apache.org/)下载`Sqoop`安装包，目前有分为2个大版本，Sqoop1、Sqoo2。   
这两个版本之间是完全不兼容的，采用的架构也不一样的。   
*这里只介绍Sqoop1的安装和使用*   

+ 解压安装包到指定目录，例如
```shell script
tar -zxvf sqoop-1.4.7.bin__hadoop-2.6.0.tar.gz -C /usr/cai/
 ```
*注意要cd到安装包的目录下在进行解压命令，否则就老老实实输入全路径*

## 配置环境变量

```shell script
vim ~/.bashrc
 ```
然后编辑这个文件
```shell script
export JAVA_HOME=/usr/local/java/jdk1.8.0_144
export PATH=$JAVA_HOME/bin:$PATH
export SQOOP_HOME=/usr/cai/sqoop-1.4.7.bin__hadoop-2.6.0
 ```

### Sqoop的配置文件
+ 修改安装包里面conf文件夹里面的配置文件，复制一份再进行修改吧
```shell script
cp sqoop-env-template.sh sqoop-env.sh
vim sqoop-env.sh
 ```
+ 然后编辑这个文件，指定hadoop的安装目录，以及其他信息，我这里只配置hadoop的安装目录，先用来演示
```shell script
export HADOOP_COMMON_HOME=/usr/cai/hadoop-2.9.2
export HADOOP_MAPRED_HOME=/usr/cai/hadoop-2.9.2
 ```
+ 然后把mysql驱动包`mysql-connector-java-5.1.31-bin.jar`，放到安装目录里面的./lib/目录下   

+ 我们测试下，进入安装目录里面的bin目录
```shell script
cd /usr/cai/sqoop-1.4.7.bin__hadoop-2.6.0/bin
./sqoop-version
 ```
提示如下
```shell script
Warning: /usr/cai/sqoop-1.4.6.bin__hadoop-2.0.4-alpha/../hbase does not exist! HBase imports will fail.
Please set $HBASE_HOME to the root of your HBase installation.
Warning: /usr/cai/sqoop-1.4.6.bin__hadoop-2.0.4-alpha/../hcatalog does not exist! HCatalog jobs will fail.
Please set $HCAT_HOME to the root of your HCatalog installation.
Warning: /usr/cai/sqoop-1.4.6.bin__hadoop-2.0.4-alpha/../accumulo does not exist! Accumulo imports will fail.
Please set $ACCUMULO_HOME to the root of your Accumulo installation.
Warning: /usr/cai/sqoop-1.4.6.bin__hadoop-2.0.4-alpha/../zookeeper does not exist! Accumulo imports will fail.
Please set $ZOOKEEPER_HOME to the root of your Zookeeper installation.
错误: 找不到或无法加载主类 org.apache.sqoop.Sqoop
 ```
> 错误: 网上找到解决办法是，把Sqoop目录下的sqoop-1.4.7.jar拷贝到hadoop的hadoop/share/hadoop/mapreduce/lib目录下。    

再次验证结果
```shell script
Warning: /usr/cai/sqoop-1.4.6.bin__hadoop-2.0.4-alpha/../hbase does not exist! HBase imports will fail.
Please set $HBASE_HOME to the root of your HBase installation.
Warning: /usr/cai/sqoop-1.4.6.bin__hadoop-2.0.4-alpha/../hcatalog does not exist! HCatalog jobs will fail.
Please set $HCAT_HOME to the root of your HCatalog installation.
Warning: /usr/cai/sqoop-1.4.6.bin__hadoop-2.0.4-alpha/../accumulo does not exist! Accumulo imports will fail.
Please set $ACCUMULO_HOME to the root of your Accumulo installation.
Warning: /usr/cai/sqoop-1.4.6.bin__hadoop-2.0.4-alpha/../zookeeper does not exist! Accumulo imports will fail.
Please set $ZOOKEEPER_HOME to the root of your Zookeeper installation.
20/05/10 01:36:24 INFO sqoop.Sqoop: Running Sqoop version: 1.4.7
Sqoop 1.4.7
git commit id 2328971411f57f0cb683dfb79d19d4d19d185dd8
Compiled by maugli on Thu Dec 21 15:59:58 STD 2017
 ```
出现一些警告是因为暂未配置一些变量的值。这个没关系，后期有使用他们的时候再配置。   
当然如果觉得警告有点犯强迫症，可以给它随便指定下它们路径。最后配置如下   
```shell script
#Set path to where bin/hadoop is available
export HADOOP_COMMON_HOME=/usr/cai/hadoop-2.9.2

#Set path to where hadoop-*-core.jar is available
export HADOOP_MAPRED_HOME=/usr/cai/hadoop-2.9.2

#set the path to where bin/hbase is available
export HBASE_HOME=/root

#Set the path to where bin/hive is available
export HIVE_HOME=/root

#Set the path for where zookeper config dir is
export ZOOCFGDIR=/root
export HCAT_HOME=/root
export ACCUMULO_HOME=/root
export ZOOKEEPER_HOME=/root
 ```
再次验证结果
```shell script
20/05/10 02:18:13 INFO sqoop.Sqoop: Running Sqoop version: 1.4.7
Sqoop 1.4.7
git commit id 2328971411f57f0cb683dfb79d19d4d19d185dd8
Compiled by maugli on Thu Dec 21 15:59:58 STD 2017
 ```
OK 安装大功告成。

## 练习
*以下脚本没有特别指明，默认都在sqoop安装目录下执行*
+ 1、查看主要帮助命令
```shell script
./bin/sqoop help  
```

+ 2、通过sqoop查看所有数据库名称的命令帮助
```shell script
./bin/sqoop help list-databases
 ```

查看mysql所有数据库名称
```shell script
./bin/sqoop list-databases --connect jdbc:mysql://127.0.0.1:3306 --username root --password 123456
 ```

+ 3、从mysql导出数据到hdfs,注意先启动hdfs
```shell script
./bin/sqoop import -m 1 \
--connect jdbc:mysql://127.0.0.1:3306/hdsqoop \   
--username root --password 123456 \
--table hd_person \
--target-dir /user/sqoop/hdperson
 ```
查看刚才导入的数据是否已经到hdfs文件系统目录下了
```shell script
hadoop fs -lsr /user/sqoop/hdperson  ##查看hdfs所有目录文件结构
 ```
我们可以看到/user/sqoop/hdperson这个目录结构如下
```shell script
drwxr-xr-x   - root supergroup          0 2020-05-10 06:48 /user
drwxr-xr-x   - root supergroup          0 2020-05-10 06:48 /user/sqoop
drwxr-xr-x   - root supergroup          0 2020-05-10 06:49 /user/sqoop/hdperson
-rw-r--r--   1 root supergroup          0 2020-05-10 06:49 /user/sqoop/hdperson/_SUCCESS
-rw-r--r--   1 root supergroup         58 2020-05-10 06:49 /user/sqoop/hdperson/part-m-00000
 ```
part-m-00000就是最终导入的文件，看下内容
```shell script
hadoop fs -cat /user/sqoop/hdperson/part-m-00000
 ```
结果
```html
lala,lala@163.com
lili,12344@qq.com
youyou,13333@sina.com
 ```
*脚本说明*
```shell script
sqoop import \
	--connect jdbc:mysql://mysql.example.com/sqoop \
	--username sqoop \
	--password sqoop \
	--table person
	--target-dir
 --connnect:  指定JDBC URL
 --username/password ：mysql 数据库的用户名
 --table ：要读取的数据库表
 ```

*其实导入过程，实际上是`sqoop`启动了一个`mapreduce`的程序，您可以在`sqoop`目录下看到一个`hd_person.java`文件，就是刚才生成出来的*

+ 4、从hdfs导出数据到mysql，直接用刚才3的例子练习，反向操作    
注意先把mysql里面的hdperson数据清空下，然后脚本如下
```shell script
./bin/sqoop export \
--connect jdbc:mysql://127.0.0.1:3306/hdsqoop \
--username root --password 123456 \
--table hd_person \
--export-dir /user/sqoop/hdperson/part-m-00000 \
--fields-terminated-by ','
 ```
完成。

*脚本说明*
```shell script
sqoop export \
   --connect jdbc:mysql://mysql.example.com/sqoop \
   --username sqoop \
   --password sqoop \
   --table cities \
   --export-dir cities
   --fields-terminated-by

 --connnect:  指定JDBC URL
 --username/password ：mysql 数据库的用户名
 --table ：要导入的数据库表
 --export-dir ：数据在HDFS 上存放目录
 ```