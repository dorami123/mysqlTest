print ('it is a test (%s,%s)',[('q','q'),('w','w')])
values=[]
values.append((1,2))
values.append((3,4))
print values
import time
ts = time.time()
for i in range(4000000):
    values.append(('zhaoxiao','1993-01-01',0,170,130))
te = time.time()
print(te-ts)

st='it is a test (%s,%s)'%('q','q')
print(st)

num=5
values=[]    
for i in range(num):
    values.append(('zhaoxiao','1993-01-01',0,170,130))

stmt="insert into test1(name, birth, id, height, weight) values('%s','%s',%d,%d,%d)"%values[0]
print(stmt)