## dataX下hive到mysql性能测试报告
测试内容：基于DataX
>* 比较load和insert在不同channel下的写入性能 
>* 比较Innodb和MyIASM两种引擎的写入性能

#### 1. 环境准备
##### 1.1 数据特征
测试数据有1000W行。

建表语句：
Hive
```hive
create table if not exists test1(
  id int,
  a date,
  b int,
  c bigint,
  d double,
  e string,
  f date,
  g string
)
 ROW FORMAT DELIMITED
   FIELDS TERMINATED BY '\t'
 STORED AS TEXTFILE;

```
Mysql
```mysql
CREATE TABLE if not exists test1(  
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,  
  `a` date NOT NULL,  
  `b` int unsigned NOT NULL,  
  `c` bigint unsigned NOT NULL,  
  `d` double unsigned NOT NULL,  
  `e` varchar(20) NOT NULL,  
  `f` TIMESTAMP NOT NULL,
  `g` blob NOT NULL , 
  PRIMARY KEY (`id`),  
  index `a` (`a`)  
) engine=MyISAM;
```
单行记录如下：
```
id:54600	
a:2017-08-22	
b:27169	
c:221936	
d:0.949828289499	
e:fake00054600	
f:2017-08-22	
g:113

```
##### 1.2机器参数

> 执行DataX的机器：
Linux gdc-gw01-testing.i.nease.net 3.2.0-4-amd64 #1 SMP Debian 3.2.65-1 x86_64 GNU/Linux

> Mysql数据库
地址：10.120.173.179
Server version: 5.5.37-0+wheezy1-log (Debian)

#### 2 测试报告

##### 2.1 比较load和insert在不同channel下的写入性能 
Innodb
<div align=center><img width="600" height="360" src="https://raw.githubusercontent.com/dorami123/mysqlTest/master/hive2mysql/pic/hive2mysql_innodb.png"/></div>

MyIASM
<div align=center><img width="600" height="360" src="https://raw.githubusercontent.com/dorami123/mysqlTest/master/hive2mysql/pic/hive2mysql_myiasm.png"/></div>

##### 2.2 比较Innodb和MyIASM两种引擎的写入性能
<div align=center><img width="600" height="360" src="https://raw.githubusercontent.com/dorami123/mysqlTest/master/hive2mysql/pic/hive2mysql.png"/></div>


**小结：**
当写入有索引的表时，MyISAM的批量insert性能要劣于InnoDB，而load性能要优于InnoDB。原因参考：

