import random

num=4000000
rr=range(num)
random.shuffle(rr)  
file=open('data.txt','w')  
for i in rr:
	if i%3==0:
		file.write("\"zhaoxiao\",\"1993-01-01\",%d,170,130\n"%i)
	elif i%3==1:
		file.write("\"xiaozhao\",\"1993-02-02\",%d,178,135\n"%i)
	else:
		file.write("\"xiaozhao\",\"1993-02-02\",%d,178,135\n"%(i-2))
file.close()