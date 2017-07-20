#!/usr/bin/env python
# -*- coding: utf-8 -*-

'''
computer with 1cpu,4kernel,4G mem 
mysql table test:
+--------+-------------+------+-----+---------+-------+
| Field  | Type        | Null | Key | Default | Extra |
+--------+-------------+------+-----+---------+-------+
| name   | varchar(20) | YES  |     | NULL    |       |
| birth  | date        | YES  |     | NULL    |       |
| id     | int(11)     | NO   |     | NULL    |       |
| height | int(11)     | YES  |     | NULL    |       |
| weight | int(11)     | YES  |     | NULL    |       |
+--------+-------------+------+-----+---------+-------+

set global net_buffer_length=1000000; 
set global max_allowed_packet=1000000000;

execute result:
num=4000000
func_mysqldb
354.47 sec
func_mysqldb1
1, 51.76 sec
5, 51.57 sec
10, 51.67 sec
20, 51.52 sec
30, 52.14 sec
50, 53.82 sec
100, 51.66 sec
200, 50.93 sec

'''

import time
# import os, time, re, threading
import MySQLdb


def timeit(method):
    def timed(*args, **kw):
        ts = time.time()
        result = method(*args, **kw)
        te = time.time()
        print ('%r, %2.2f sec' % (method.__name__, te-ts))
        return result
    return timed    

def clear_table():
    try:
        conn=MySQLdb.connect(host='localhost',user='root',passwd='cloudera',db='test',port=3306)
        cur=conn.cursor()
        cur.execute("truncate test1")
        cur.close()
        conn.close()
    except MySQLdb.Error,e:
        print "Mysql Error %d: %s" % (e.args[0], e.args[1])

# 每次insert一句
@timeit
def func_mysqldb(num,values):
    try:
        conn=MySQLdb.connect(host='localhost',user='root',passwd='cloudera',db='test',port=3306)
        cur=conn.cursor()

        for i in range(num):
            stmt="insert into test1(name, birth, id, height, weight) values('%s','%s',%d,%d,%d)"%values[i]
            cur.execute(stmt)
        conn.commit()
    except MySQLdb.Error,e:
        print "Mysql Error %d: %s" % (e.args[0], e.args[1])
    cur.close()
    conn.close()

# 一次性insert
@timeit
def func_mysqldb2(values):
    try:
        conn=MySQLdb.connect(host='localhost',user='root',passwd='cloudera',db='test',port=3306)
        cur=conn.cursor()
        # mysqldb的占位符统一写为%s,区别于python
        stmt="insert into test1(name, birth, id, height, weight) values(%s,%s,%s,%s,%s)"
        # 该语句对insert做专门的优化,相当于执行一条insert: 
        cur.executemany(stmt,values)
        conn.commit()
    except MySQLdb.Error,e:
        print "Mysql Error %d: %s" % (e.args[0], e.args[1])
        conn.rollback()
    cur.close()
    conn.close()

# 分批insert,优化
@timeit
def func_mysqldb1(num,div,values):
    try:
        conn=MySQLdb.connect(host='localhost',user='root',passwd='cloudera',db='test',port=3306)
        cur=conn.cursor()
        # mysqldb的占位符统一写为%s,区别于python
        stmt="insert into test1(name, birth, id, height, weight) values(%s,%s,%s,%s,%s)"
        
        for i in range(div):
            # 该语句对insert做专门的优化,相当于执行一条insert: 
            cur.executemany(stmt,values[num/div*i:num/div*(i+1)])
        conn.commit()
    except MySQLdb.Error,e:
        print "Mysql Error %d: %s" % (e.args[0], e.args[1])
        conn.rollback()
    cur.close()
    conn.close()

if __name__ == '__main__':
    num=4000000
    values=[]    
    for i in range(num):
        values.append(('zhaoxiao','1993-01-01',0,170,130))

    clear_table()
    func_mysqldb(num,values)
    # clear_table()
    # func_mysqldb2(values)
    # for i in [5,10,20,30,50,100,200]:
    #     clear_table()
    #     func_mysqldb2(num,i,values)
    # func_mysqldb1(num,1,values)

