# mysql读入性能测试
对mysql读入性能进行测试，主要对以下项目进行了测试：

1.insert批量读入时，不同batchsize的性能；

2.load和批量insert性能比较；

3.两种mysql引擎下load性能的比较；

4.使用java和python的读入的比较(空表和非空表)；

测试使用mysql 5.1.73, 内存4G, CPU 3.1 GHz Intel Core i7

以上比较使用了三种表：test1无索引,test2有两个索引,test3有两个索引，其中一个为唯一键。详细请看[buildTable.sql](https://github.com/dorami123/mysqlTest/blob/master/buildTable.sql)

测试用数据有100000条，使用[generateData.py](https://github.com/dorami123/mysqlTest/blob/master/generateData.py)生成，其中1/3数据的id重复。

#1.insert批量读入时，不同batchsize的性能；
![1](https://github.com/dorami123/mysqlTest/blob/master/graph/batchsize_InnoDB.png)
![2](https://github.com/dorami123/mysqlTest/blob/master/graph/batchsize_MyISAM.png)

#2.load和批量insert性能比较；
![3](https://github.com/dorami123/mysqlTest/blob/master/graph/loadVsInsert%20InnoDB.png)

