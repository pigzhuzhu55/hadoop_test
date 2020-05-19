<a href="https://github.com/pigzhuzhu55/hadoop_test">返回目录</a>
## 简介
Hadoop是一个开源的、能够对大量数据进行分布式处理的软件框架。具有可靠的、高效的、可伸缩的等特点。  
Hadoop的构成
* 分布式存储系统HDFS（Hadoop Distributed File System） 
  - 提供了高可靠性、高扩展性和高吞吐率的数据存储服务 
* 资源管理系统YARN（Yet Another Resource Negotiator） 
  - 负责集群资源的统一管理和调度 
* 分布式计算框架MapReduce 
  - 具有易于编程、高容错性和高扩展性等优点 
## 安装
### 下载安装包
首先，去[官网](http://hadoop.apache.org/)下载hadoop安装包，我这里下载的是[2.9.2版本](https://www.apache.org/dyn/closer.cgi/hadoop/common/hadoop-2.9.2/hadoop-2.9.2.tar.gz)

### 搭建hadoop分布式环境
准备三台虚拟机，设置静态IP 192.168.100.101 192.168.100.102 192.168.100.103。   
修改hosts文件，设置ip和主机名对映关系，比如：   
   + 192.168.100.101 hm
   + 192.168.100.102 hs1
   + 192.168.100.103 hs2
删除hadoop安装目录下的tmp目录   

### 设置集群间的免密登陆
#### 分别在三台主机上
安装ssh服务，具体自行百度。  
修改sshd的配置文件， `vim /etc/ssh/sshd_config`,找到下面三项，并把前面的注释#去掉   

+ RSAAuthentication yes
+ PubkeyAuthentication yes
+ AuthorizedKeysFile	.ssh/authorized_keys .ssh/authorized_keys2   

重启ssh服务 `service sshd restart`    
生成无密码密钥对 `ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa `   
#### （hm主机 192.168.100.101，以下统一叫master）
+ 在master上将公钥追加到hdfs用户的授权keys中 `cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys`   
在master上修改授权keys文件的权限为600 `chmod 600 ~/.ssh/authorized_keys`  
在master上验证本机hdfs用户是否可以无密码ssh连接了`ssh localhost`    

+ 将2台从节点(hs1、hs2)上面的公钥,远程拷贝到master上   
在hs1主机上，命令 `scp ~/.ssh/id_rsa.pub hadoop@hm:~/.ssh/id_rsa.pub_from_hs1`   
在hs2主机上，命令 `scp ~/.ssh/id_rsa.pub hadoop@hm:~/.ssh/id_rsa.pub_from_hs2`  
接着在master上，命令    
`cat ~/.ssh/id_rsa.pub_from_hs1 authorized_keys`  
`cat ~/.ssh/id_rsa.pub_from_hs2 authorized_keys` 
这样，这个authorized_keys就有三台主机的公钥了。

+ 然后把master主机上的这个authorized_keys(合并了三台机器的公钥文件)分发给另外两台主机(hs1、hs2)  
`scp ~/.ssh/authorized_keys hadoop@hs1:~/.ssh/authorized_keys`  
`scp ~/.ssh/authorized_keys hadoop@hs2:~/.ssh/authorized_keys` 
   
+ 都重启ssh服务 `service sshd restart`  

### 修改hadoop配置文件
- *core-site.xml*
- *mapred-site.xml*
- *yarn-site.xml*
- *hdfs-site.xml*
- *hadoop-env.cmd*  
*上面这几个配置文件，我放在代码里面了（单机版的配置）分布式配置的不同地方如下* 
#### 修改 core-site.xml
```xml
<configuration>	
    <property>
		<name>fs.defaultFS</name>
		<value>hdfs://hm:9000</value>	
	</property>
	<property>
		<name>hadoop.tmp.dir</name>
		<value>/usr/cai/hadoop-2.9.2/tmp</value>
	</property>
</configuration>
```

#### 修改slaves文件，添加从节点信息
```shell script
hs1
hs2
```

#### 修改 yarn-site.xml
```xml
<configuration>
		<property>
			<name>yarn.resourcemanager.hostname</name>
			<value>hm</value>
		</property>
		<property>
			<name>yarn.nodemanager.aux-services</name>
			<value>mapreduce_shuffle</value>
		</property>
</configuration>
```

### 格式化主节点上的hdfs
进入hadoop的bin目录 `./hdfs namenode -format`  

### 启动集群 
进入hadoop的sbin目录 `./start-all.sh`  
命令 `jps` 查看服务列表

### windows下编程开发 
### 01、搭建hadoop本地开发环境
解压安装包到本地windows目录下，更改配置文件
- *core-site.xml*
- *mapred-site.xml*
- *yarn-site.xml*
- *hdfs-site.xml*
- *hadoop-env.cmd*  
如工程里面所示例。

### 02、配置本地windows环境变量
HADOOP_HOME 设置为你解压hadoop源文件的目录，比如  D:\User\hadoop-2.9.2。  
HADOOP_USER_NAME 设置为 hadoop(有目录修改权限的账号名称),当然也要配置好java的环境变量，这是前提。


### 03、导入maven项目，hadoop_test项目到idea, 编码开发。
代码示例见工程里 mapreduce_test文件夹下


