## dataX下hive到mysql性能测试报告
测试内容：基于DataX
>* 比较load和insert在不同channel下的写入性能 
>* 比较Innodb和MyIASM两种引擎的写入性能

### 1. 环境准备
#### 1.1 数据特征
测试数据有1000W行，batchsize=10w，建表语句如下：

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
#### 1.2机器参数

执行DataX的机器：

osInfo: Oracle Corporation 1.7 24.65-b04

jvmInfo: Linux amd64 3.2.0-4-amd64

cpu num: 8

totalPhysicalMemory: 47.26G

freePhysicalMemory: 7.71G

maxFileDescriptorCount: 65535

currentOpenFileDescriptorCount: 42

Mysql数据库

地址：10.120.173.179
Server version: 5.5.37-0+wheezy1-log (Debian)

#### 1.3 DataX配置

DataX jvm 参数：-Xms1G -Xmx1G

[core.json设置(全部默认)](https://github.com/alibaba/DataX/blob/master/core/src/main/conf/core.json)

任务配置：

```
{
    "job": {
        "content": [
            {
                "reader": {
                    "name": "hivereader",
                    "parameter": {
                        "principal": "hadoop/_HOST@NIE.NETEASE.COM",
                        "keytab": "",
                        "dataOrigin": {
                            "database": "zhaoxiao",
                            "table": "test",
                            "partitions": [
                                {
                                    "name": "",
                                    "partitionType": "",
                                    "feature": "",
                                    "parameters": []
                                }
                            ],
                            "fields": [],
                            "transform_fields": [
                                {
                                    "name": "",
                                    "transformer": ""
                                }
                            ]
                        },
                        "hadoopConfig": {
                            "yranRM": ""
                        }
                    }
                },
                "writer": {
                    "name": "mysqlwriter",
                    "parameter": {
                        "batchsize":"100000",
                        "column": [
                            "*"
                        ],
                        "connection": [
                            {
                                "jdbcUrl": "jdbc:mysql://",
                                "table": [
                                    "test1"
                                ]
                            }
                        ],
                        "password": "****",
                        "preSql": [
                            "truncate table @table"
                        ],
                        "session": [ 
                        ],
                        "username": "***",
                        "writeMode": "load",
                        "loadMode": "ignore"
                    }
                }
            }
        ],
        "setting": {
            "speed": {
                "channel": "1"
            }
        }
    }
}

```


### 2 测试报告

#### 2.1 比较load和insert在不同channel下的写入性能 
Innodb
<div align=center><img width="500" height="300" src="https://raw.githubusercontent.com/dorami123/mysqlTest/master/hive2mysql/pic/hive2mysql_innodb.png"/></div>

MyIASM
<div align=center><img width="500" height="300" src="https://raw.githubusercontent.com/dorami123/mysqlTest/master/hive2mysql/pic/hive2mysql_myiasm.png"/></div>

**小结：**
在单线程写时，load的性能要优于insert。在多线程写时，insert的性能有比较大的提升，这时用insert性能更好。
#### 2.2 比较Innodb和MyIASM两种引擎的写入性能
<div align=center><img width="500" height="300" src="https://raw.githubusercontent.com/dorami123/mysqlTest/master/hive2mysql/pic/hive2mysql.png"/></div>

**小结：**
在并发写入时，使用MyIASM引擎时，insert性能要优于使用Innodb引擎。

### 3 总结
a. 单线程写入时，使用load的性能要优于insert; 在使用datax并发写入时,宜采用insert方法，此时的性能与并发数基本成正比；

b. MyISAM并发写的效率要略高于Innodb。使用MyISAM并发时需要设置concurrent_insert。


**其他：**

a.关于MyIASM的并发支持

通常来说，在MyISAM里读写操作是串行的，但当对同一个表进行查询和插入操作时，为了降低锁竞争的频率，根据concurrent_insert的设置，MyISAM是可以并行处理查询和插入的：

当concurrent_insert=0时，不允许并发插入功能。

当concurrent_insert=1时，允许对没有洞的表使用并发插入，新数据位于数据文件结尾（缺省）。

当concurrent_insert=2时，不管表有没有洞，都允许在数据文件结尾并发插入。

b.关于写入效率

当线程数从3到4时，写入效率没有改善，这是因为reader端在切分时，有一定的数据倾斜，使得单
个线程的速度比较慢。
