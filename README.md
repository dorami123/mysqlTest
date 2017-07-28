# mysql读入性能测试
对mysql读入性能进行测试，主要对以下项目进行了测试：

1.insert批量读入时，不同batchsize的性能；

2.load和批量insert性能比较；

3.两种mysql引擎下load性能的比较；

4.使用java和python的读入的比较(空表和非空表)；

测试使用mysql 5.1.73, 内存4G, CPU 3.1 GHz Intel Core i7。

使用了三种表：table1无索引,table2有两个索引,table3有两个索引，其中一个为唯一键。详细请看[buildTable.sql](https://github.com/dorami123/mysqlTest/blob/master/buildTable.sql)。

测试用数据有100000条，使用[generateData.py](https://github.com/dorami123/mysqlTest/blob/master/generateData.py)生成，其中1/3数据的id重复。

# 1.insert批量读入时，不同batchsize的性能；
注：写到空表里.

纵轴为每秒写入的行数.横轴表示将10万行数据分为[1,10,100,1000,10000,100000]份insert入数据库。
##### 使用InnoDB引擎
<div align=center><img width="640" height="240" src="https://github.com/dorami123/mysqlTest/blob/master/graph/batchsize_InnoDB.png"/></div>

##### 使用MyISAM引擎
<div align=center><img width="640" height="240" src="https://github.com/dorami123/mysqlTest/blob/master/graph/batchsize_MyISAM.png"/></div>

# 2.load和批量insert性能比较(空表和非空表)；
注:load1表示load ignore,load2表示load replace
##### 使用InnoDB引擎，比较批量insert和load ignore,load replace
<div align=center><img width="640" height="240" src="https://github.com/dorami123/mysqlTest/blob/master/graph/loadVsInsert%20InnoDB.png"/></div>

##### 使用MyISAM引擎，比较批量insert和load ignore,load replace
<div align=center><img width="640" height="240" src="https://github.com/dorami123/mysqlTest/blob/master/graph/loadVsInsert%20MyISAM.png"/></div>

##### 两种引擎下，插入数据到已经有10万条数据的表test3，比较批量insert和load ignore的性能
<div align=center><img width="400" height="240" src="https://github.com/dorami123/mysqlTest/blob/master/graph/loadVsInsert2.png"/></div>

# 3.两种mysql引擎下load性能的比较；
注：写到空表里
##### 比较批量insert和load ignore,load replace分别在两种引擎上的效果
<div align=center><img width="640" height="480" src="https://github.com/dorami123/mysqlTest/blob/master/graph/loadVsInsert%20MyISAM%20vs%20InnoDB.png"/></div>

# 4.使用java和python的读入的比较；

##### 两种引擎下，使用空表test3，比较python和java的写性能
<div align=center><img width="640" height="480" src="https://github.com/dorami123/mysqlTest/blob/master/graph/MyISAM%20vs%20InnoDB.png"/></div>


