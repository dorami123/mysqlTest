# -*- coding: utf-8 -*-
# print ('it is a test (%s,%s)',[('q','q'),('w','w')])
# values=[]
# values.append((1,2))
# values.append((3,4))
# print values
# import time
# ts = time.time()
# for i in range(4000000):
#     values.append(('zhaoxiao','1993-01-01',0,170,130))
# te = time.time()
# print(te-ts)

# st='it is a test (%s,%s)'%('q','q')
# print(st)

# num=5
# values=[]    
# for i in range(num):
#     values.append(('zhaoxiao','1993-01-01',0,170,130))

# stmt="insert into test1(name, birth, id, height, weight) values('%s','%s',%d,%d,%d)"%values[0]
# print(stmt)


# file=open("data.txt")
# line=file.readlines(10)

# print(line)

# import MySQLdb
# file=open('data.txt')
# lines=file.readlines(1)
# values=[]
# for line in lines:
# 	l=line.rstrip('\n').split(',')
# 	ll=(l[0][1:-1],l[1][1:-1],int(l[2]),int(l[3]),int(l[4]))
# 	values.append(ll)

# def insertAll(values):
#     try:
#         conn=MySQLdb.connect(host='localhost',user='root',passwd='cloudera',db='test',port=3306)
#         cur=conn.cursor()
#         # mysqldb的占位符统一写为%s,区别于python
#         stmt="insert into test1(name, birth, id, height, weight) values(%s,%s,%s,%s,%s)"
#         # 该语句对insert做专门的优化,相当于执行一条insert: 
#         cur.executemany(stmt,values)
#         conn.commit()
#     except MySQLdb.Error,e:
#         print "Mysql Error %d: %s" % (e.args[0], e.args[1])
#         conn.rollback()
#     cur.close()
#     conn.close()

# insertAll(values)
mode='ll'
tableName='ddd'
stmt=("load data infile '/mnt/mysqlTest/data.txt' %s into table %s" 
 " FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '\"' LINES TERMINATED BY '\n'"%(mode,tableName)
)
print(stmt)