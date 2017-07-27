
# -*- coding: utf-8 -*-

'''
computer with 1cpu,2kernel,4G mem 
mysql table test1:
+--------+-------------+------+-----+---------+-------+
| Field  | Type        | Null | Key | Default | Extra |
+--------+-------------+------+-----+---------+-------+
| name   | varchar(20) | YES  |     | NULL    |       |
| birth  | date        | YES  |     | NULL    |       |
| id     | int(11)     | NO   |     | NULL    |       |
| height | int(11)     | YES  |     | NULL    |       |
| weight | int(11)     | YES  |     | NULL    |       |
+--------+-------------+------+-----+---------+-------+

mysql table test2:
+--------+-------------+------+-----+---------+-------+
| Field  | Type        | Null | Key | Default | Extra |
+--------+-------------+------+-----+---------+-------+
| name   | varchar(20) | YES  | mul | NULL    |       |
| birth  | date        | YES  |     | NULL    |       |
| id     | int(11)     | NO   | mul | NULL    |       |
| height | int(11)     | YES  |     | NULL    |       |
| weight | int(11)     | YES  |     | NULL    |       |
+--------+-------------+------+-----+---------+-------+

mysql table test3:
+--------+-------------+------+-----+---------+-------+
| Field  | Type        | Null | Key | Default | Extra |
+--------+-------------+------+-----+---------+-------+
| name   | varchar(20) | YES  | mul | NULL    |       |
| birth  | date        | YES  |     | NULL    |       |
| id     | int(11)     | NO   | uni | NULL    |       |
| height | int(11)     | YES  |     | NULL    |       |
| weight | int(11)     | YES  |     | NULL    |       |
+--------+-------------+------+-----+---------+-------+

set global net_buffer_length=1000000; 
set global max_allowed_packet=1000000000;

execute result:

num=4000000  table1
insertPerline
354.47 sec
insertBatch
1, 51.76 sec
5, 51.57 sec
10, 51.67 sec
20, 51.52 sec
30, 52.14 sec
50, 53.82 sec
100, 51.66 sec
200, 50.93 sec
--------------
loadAll test1   test2  test3
        11.81   326.3  locks exceed the lock table size
'''

import time
import MySQLdb


# def timeit(method):
#     def timed(*args, **kw):
#         ts = time.time()
#         result = method(*args, **kw)
#         te = time.time()
#         print ('%r, table: %s ,%2.2f sec' % (method.__name__,args[0],te-ts))
#         return result
#     return timed    

#     public static void print(String key,long startTime,long endTime){
#         System.out.println("每执行"+count+"次sql提交一次事务");
#         System.out.println(key+"，用时"+ (endTime-startTime)/1000.0+" s,平均每秒执行"+(count*1000/(endTime-startTime))+"条");
#         System.out.println("----------------------------------");
#     }

def printResults(key,ts,te):
    print(key+",用时"+ (te-ts)+" s,平均每秒执行"+(40000/(te-ts))+"条")
    print("----------------------------------")

def getConn():
    try:
        ts=time.time()
        conn=MySQLdb.connect(host='localhost',user='root',passwd='cloudera',db='test',port=3306)
        te=time.time()
        print("创建连接用时%d s"%(te-ts))
        return conn
    except MySQLdb.Error,e:
        print "Mysql Error %d: %s" % (e.args[0], e.args[1])        


def clear_table(tableName):
    try:
        conn=MySQLdb.connect(host='localhost',user='root',passwd='cloudera',db='test',port=3306)
        cur=conn.cursor()
        cur.execute("truncate %s"%tableName)
        conn.commit()
        cur.close()
        conn.close()
        print("执行清理操作成功")
    except MySQLdb.Error,e:
        print "Mysql Error %d: %s" % (e.args[0], e.args[1])

# 每次insert一句
# @timeit
def insertPerline(tableName,values,mode='ignore'):
    num=len(values)
    conn=getConn()
    try:
        # conn=MySQLdb.connect(host='localhost',user='root',passwd='cloudera',db='test',port=3306)
        ts=time.time()
        cur=conn.cursor()

        for i in range(num):
            stmt1="insert %s into %s"%(mode,tableName)
            stmt=stmt1+" values('%s','%s',%d,%d,%d)"%values[i]
            cur.execute(stmt)
        conn.commit()
        te=time.time()
        printResults("MySql依次插入4万条记录到"+tableName,ts,te)
    except MySQLdb.Error,e:
        print "Mysql Error %d: %s" % (e.args[0], e.args[1])
    cur.close()
    conn.close()

# 分批insert,优化
# @timeit
def insertBatch(tableName,div,values,mode='ignore'):
    num=len(values)
    conn=getConn()
    try:
        ts=time.time()
        # conn=MySQLdb.connect(host='localhost',user='root',passwd='cloudera',db='test',port=3306)
        cur=conn.cursor()
        # mysqldb的占位符统一写为%s,区别于python
        stmt1="insert %s into %s"%(mode,tableName) 
        stmt=stmt1+" values(%s,%s,%s,%s,%s)"
        for i in range(div):
            # 该语句对insert做专门的优化,相当于执行一条insert: 
            cur.executemany(stmt,values[num/div*i:num/div*(i+1)])
        conn.commit()
        te=time.time()
        printResults("MySql批量插入4万条记录到"+tableName,ts,te)
    except MySQLdb.Error,e:
        print "Mysql Error %d: %s" % (e.args[0], e.args[1])
        conn.rollback()
    cur.close()
    conn.close()

# 一次性load
# @timeit
def loadAll(tableName,mode='ignore',disable=False):
    conn=getConn()
    try:
        ts=time.time()
        # conn=MySQLdb.connect(host='localhost',user='root',passwd='cloudera',db='test',port=3306)
        cur=conn.cursor()
        stmt=("load data infile '/mnt/mysqlTest/data.txt' %s into table %s" 
         " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\n'"%(mode,tableName)
        )            
        cur.execute(stmt)
        conn.commit()
        te=time.time()
        printResults("MySql一次load ("+mode+") 4万条记录到 "+tableName,ts,te)
    except MySQLdb.Error,e:
        print "Mysql Error %d: %s" % (e.args[0], e.args[1])
        conn.rollback()
    cur.close()
    conn.close()


if __name__ == '__main__':
    tableName=['test1','test2','test3']
    
    file=open('../data.txt')
    lines=file.readlines()
    file.close()
    values=[]

    for line in lines:
        l=line.rstrip('\n').split(',')
        ll=(l[0][1:-1],l[1][1:-1],int(l[2]),int(l[3]),int(l[4]))
        values.append(ll)
    num=len(values)

    for i in range(3):
        clear_table(tableName[i])
        insertPerline(tableName[i],values)

        clear_table(tableName[i])
        insertBatch(tableName[i],5,values)

        clear_table(tableName[i])
        loadAll(tableName[i])

        clear_table(tableName[i])
        loadAll(tableName[i],'replace')
    # insertBatch(tableName[0],5,values)
    # insertBatch(5,values,tableName)
    # loadAll(tableName[2],'ignore')
    # clear_table()
    # func_mysqldb2(values)
    # for i in [5,10,20,30,50,100,200]:
    #     clear_table()
    #     func_mysqldb2(num,i,values)
    # func_mysqldb1(num,1,values)

