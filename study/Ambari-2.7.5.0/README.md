<a href="https://github.com/pigzhuzhu55/hadoop_test">返回目录</a>
## 简介
### Ambair介绍
+ `Apache Ambari`是一个用于支持大数据软件供应、管理与监控的软件。   
+ 它也是一个分布式软件,分为`Ambair-Server`与`Ambari-Client`两个部分。   
+ 在生产环境下一般单独用一台服务器安装`ambari-server`来确保服务的稳定性。然后需要安装大数据服务组件的服务器上均安装`ambari-client`组件,server发送命令与client进行交互完成任务。

### HDP介绍
+ `HDP`是`hortonworks`公司的`Hadoop`发行版,其中包括大多数`Hadoop`生态下的重要组件,可以提供给`Ambari`进行部署与安装。   
+ 从[官网文档中](https://docs.cloudera.com/HDPDocuments/)上可以查看HDP中版本与其他组件的版本对应关系。比如查看3.1.5版本下的对应关系 https://docs.cloudera.com/HDPDocuments/HDP3/HDP-3.1.5/release-notes/content/comp_versions.html
+ 以及查看每个产品版本的对照表 `https://supportmatrix.hortonworks.com/`

## 准备安装包
+ 去官网文档中,找到最新资源,[Ambair2.7.5文档](https://docs.cloudera.com/HDPDocuments/Ambari-2.7.5.0/bk_ambari-installation/content/ambari_repositories.html)。
    * `Ambair`[2.7.5.0](https://archive.cloudera.com/p/ambari/ubuntu18/2.x/updates/2.7.5.0/ambari-2.7.5.0-ubuntu18.tar.gz)
    * `HDP`[3.1.5.0](https://archive.cloudera.com/p/HDP/ubuntu18/3.x/updates/3.1.5.0/HDP-3.1.5.0-ubuntu18-deb.tar.gz)
    * `HDP-UTILS`[1.1.0.22](https://archive.cloudera.com/p/HDP-UTILS/1.1.0.22/repos/ubuntu18/HDP-UTILS-1.1.0.22-ubuntu18.tar.gz)
    * `HDP-GPL`[3.1.5.0](https://archive.cloudera.com/p/HDP-GPL/ubuntu18/3.x/updates/3.1.5.0/HDP-GPL-3.1.5.0-ubuntu18-gpl.tar.gz)
   
+ *目前最新版官网好像需要登陆并具有相关的权限才能下载，登陆地址 https://sso.cloudera.com/ 反正我是没权限，没办法了，百度找到另外一个版本的下载源*

    * `Ambair`[2.7.3.0](http://public-repo-1.hortonworks.com/ambari/ubuntu18/2.x/updates/2.7.3.0/ambari-2.7.3.0-ubuntu18.tar.gz)
    * `HDP`[3.1.0.0](http://public-repo-1.hortonworks.com/HDP/ubuntu18/3.x/updates/3.1.0.0/HDP-3.1.0.0-ubuntu18-deb.tar.gz)
    * `HDP-UTILS`[1.1.0.22](http://public-repo-1.hortonworks.com/HDP-UTILS-1.1.0.22/repos/ubuntu18/HDP-UTILS-1.1.0.22-ubuntu18.tar.gz)
    * `HDP-GPL`[3.1.0.0](http://public-repo-1.hortonworks.com/HDP-GPL/ubuntu18/3.x/updates/3.1.0.0/HDP-GPL-3.1.0.0-ubuntu18-gpl.tar.gz)

### mysql-jdbc驱动
在Ambari服务器主机上，准备适当的连接器/JDBC驱动程序文件，以便以后部署。我这里暂时放在/var/lib/3目录下   
我例子里面放的是ubuntu18.04环境下的jar，更多查看 https://dev.mysql.com/downloads/connector/j/

## 配置环境要求并安装`ambari-server`

### 安装`apache`服务器
```shell script
sudo apt-get install apache2
mkdir -p /var/www/html
 ```
然后把上面下载好的四个安装包，放在apache服务站点的根目录下，即`/var/www/html`,然后解压。

### 配置本地安装源`Ambair`、`HDP`
在/etc/apt/sources.list.d目录下，新增文件`ambari.list`、`ambari-hdp.list`
```shell script
echo 'deb http://127.0.0.1/ambari/ubuntu18/2.7.3.0-139/ Ambari main' > /etc/apt/sources.list.d/ambari.list

echo 'deb http://127.0.0.1/HDP-GPL/ubuntu18/3.1.0.0-78/ HDP-GPL main
deb http://127.0.0.1/HDP-UTILS/ubuntu18/1.1.0.22/ HDP-UTILS main
deb http://127.0.0.1/HDP/ubuntu18/3.1.0.0-78/ HDP main'\
 > /etc/apt/sources.list.d/ambari-hdp.list
 ```

### 安装mysql数据库
*Ambair 默认使用PostgreSQL，因为我习惯用mysql，所以这里选用mysql作为Ambair的数据存储。*
安装mysql数据库三件套
```shell script
sudo apt-get install mysql-server
sudo apt-get install mysql-client
sudo apt-get install libmysqlclient-dev
 ```
查看备用密码
```shell script
sudo vim /etc/mysql/debian.cnf
 ```
debian-sys-maint 就是备用用户名，下面就是密码。
```shell script
# Automatically generated for Debian scripts. DO NOT TOUCH!
[client]
host     = localhost
user     = debian-sys-maint
password = mIjrpM8MJxlh5c74
socket   = /var/run/mysqld/mysqld.sock
[mysql_upgrade]
host     = localhost
user     = debian-sys-maint
password = mIjrpM8MJxlh5c74
socket   = /var/run/mysqld/mysqld.sock

 ```
登陆mysql，其实好像可以直接用root登陆，好像这种安装方式，默认密码是空
```shell script
mysql -u debian-sys-maint -p 
 ```
修改mysql数据库root的密码，默认root好像是不用密码，为了演示方便，我设置为123456    
设置mysql允许远程登陆
```mysql
 grant all privileges on *.* to root@'%' with grant option;
 update mysql.user set authentication_string=password('123456') where user='root';  
 update mysql.user set plugin="mysql_native_password";
 flush privileges;
 exit;
```
重启mysql服务
```shell script
sudo /etc/init.d/mysql restart
 ```

### 安装`ambari-server`
```shell script
apt-get install ambari-server
 ````
ambari配置，这里设置jdk、数据库(我选择的是mysql)、数据库驱动mysql-jdbc的路径【上面步骤有】
```shell script
ambari-server setup
````
*在启动服务之前，记得先创建数据库,默认名字为`ambari`，并初始化建库脚本，脚本默认在`/var/lib/ambari-server/resources/`下，mysql选择`Ambari-DDL-MySQL-CREATE.sql`*
```mysql
CREATE DATABASE `ambari`
## 初始化脚本...
## ...
````
### 启动`ambari-server`
```shell script
ambari-server start 
````

外网打开8080端口，输入账号密码admin/admin,到这里`Ambari`的安装流程就告一段落

## 其他一些官方的建议设置的步骤

### 设置SSH 免密登陆
在Ambari服务器主机上生成公共和私有SSH密钥。
```shell script
ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa 
 ```
生成了`id_rsa` `id_rsa.pub`，将SSH公钥（id_rsa.pub）复制到目标主机上的根帐户，我这里只演示一台主机，无需复制
将SSH公钥添加到目标主机上的授权密钥文件中。
```shell script
 cat id_rsa.pub >> authorized_keys
```
根据您的SSH版本，您可能需要在目标主机上对.SSH目录（设置为700）和该目录中的authorized_keys文件（设置为600）设置权限。
```shell script
 chmod 700 ~/.ssh
 chmod 600 ~/.ssh/authorized_keys
```
验证是否可以免密登陆
```shell script
ssh localhost
ssh root@127.0.0.1
```
以上是单机版的免密登陆操作步骤，集群间免密参照本项目hadoop2.9.2安装的章节里面有。

### 设置集群时钟同步
*如果是单机演示安装，无需此步骤*
集群中所有节点的时钟以及运行浏览器（通过浏览器访问Ambari Web界面）的计算机必须能够彼此同步。
要安装NTP服务并确保它在启动时启动，请在每个主机上运行以下命令：
```shell script
apt-get install ntp
update-rc.d ntp defaults
```

### 最大打开文件要求
建议打开的文件描述符的最大数目为10000个或更多。要检查当前设置的最大打开文件描述符数，执行以下shell命令：
```shell script
ulimit -Sn
ulimit -Hn
 ```
设置输出不大于10000：
```shell script
ulimit -n 10000
 ```

## 用Ambari安装配置其他组件

*注意主机的hostname最好跟hosts的配置的一致，否则安装时候可能会提示一些问题*
   
> 参考文章 https://blog.csdn.net/weijiasheng/article/details/104792428/


