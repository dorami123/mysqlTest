# mysql读入性能测试
对mysql读入性能进行测试，主要对以下项目进行了测试：

1.insert批量读入时，不同batchsize的性能；

2.load和批量insert性能比较；

3.两种mysql引擎下load性能的比较；

4.使用java和python的读入的比较(空表和非空表)；

测试使用mysql 5.1.73, 内存4G, CPU 3.1 GHz Intel Core i7。

使用了三种表：table1无索引,table2有两个索引,table3有两个索引，其中一个为唯一键。详细请看[buildTable.sql](https://github.com/dorami123/mysqlTest/blob/master/buildTable.sql)。

测试用数据有100000行，使用[generateData.py](https://github.com/dorami123/mysqlTest/blob/master/generateData.py)生成，其中1/3数据的id重复。

# 1.insert批量读入时，不同batchsize的性能；
注：写到空表里.

纵轴为每秒写入的行数。横轴表示不同的batchSize，分别为[100000,10000,1000,100,10,1]，单位为条。
##### 使用InnoDB引擎
<div align=center><img width="640" height="240" src="https://github.com/dorami123/mysqlTest/blob/master/graph/batchsize_InnoDB.png"/></div>

##### 使用MyISAM引擎
<div align=center><img width="640" height="240" src="https://github.com/dorami123/mysqlTest/blob/master/graph/batchsize_MyISAM.png"/></div>

##### 小结：
当写入无索引的表时，batchsize=1000条时表现出较优的性能；写入有索引的表时，在测试的范围内，batchsize越大，性能越好。需要测试更大的数据量。

# 2.load和批量insert性能比较(空表和非空表)；

注:load1表示load ignore,load2表示load replace, insert batchSize 为1万条。
##### 使用InnoDB引擎，比较批量insert和load ignore,load replace
<div align=center><img width="640" height="240" src="https://github.com/dorami123/mysqlTest/blob/master/graph/loadVsInsert%20InnoDB.png"/></div>
比较load ignore和批量insert:插入无索引的table1时，load ignore的速度是insert的三倍；插入有索引的table2,load ignore的速度是insert的近1.5倍。插入有唯一索引的table3时，load ignore仍优于insert。

比较load ignore和replace:当有唯一索引时，因为发生替换，load replace的性能较ignore有较多下降。

##### 使用MyISAM引擎，比较批量insert和load ignore,load replace
<div align=center><img width="640" height="240" src="https://github.com/dorami123/mysqlTest/blob/master/graph/loadVsInsert%20MyISAM.png"/></div>
MyISAM引擎的规律和InnoDB规律相似，但是MyISAM引擎的load性能要远远优于批量insert。

##### 两种引擎下，插入数据到已经有6万余条数据的表table3，比较批量insert和load ignore的性能
<div align=center><img width="400" height="240" src="https://github.com/dorami123/mysqlTest/blob/master/graph/loadVsInsert2.png"/></div>

##### 小结：
无论是空表还是非空表，load ignore的性能都优于insert。性能的提升根据引擎的类型有所不同。

# 3.两种mysql引擎下load性能的比较；
注：写到空表里
##### 比较批量insert和load ignore,load replace分别在两种引擎上的效果
<div align=center><img width="640" height="480" src="https://github.com/dorami123/mysqlTest/blob/master/graph/loadVsInsert%20MyISAM%20vs%20InnoDB.png"/></div>

##### 小结：
当写入有索引的表时，MyISAM的批量insert性能要劣于InnoDB，而load性能要优于InnoDB。原因参考：

[If you use LOAD DATA INFILE on an empty MyISAM table, all nonunique indexes are created in a separate batch (as for REPAIR TABLE). Normally, this makes LOAD DATA INFILE much faster when you have many indexes. ](https://dev.mysql.com/doc/refman/5.7/en/load-data.html)
# 4.使用java和python的读入的比较；

##### 两种引擎下，使用空表test3，比较python和java的写性能
<div align=center><img width="640" height="480" src="https://github.com/dorami123/mysqlTest/blob/master/graph/MyISAM%20vs%20InnoDB.png"/></div>

##### 小结：
显而易见，使用python和java访问mysql，性能接近。

