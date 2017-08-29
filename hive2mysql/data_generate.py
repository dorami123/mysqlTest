# import string
import random
import time

# id                  	int                 	                    
# a                   	date                	                    
# b                   	int                 	                    
# c                   	bigint              	                    
# d                   	double              	                    
# e                   	varchar(20)         	                    
# f                   	timestamp           	                    
# g                   	binary

def generate():
    for i in range(10000000):
        yield str(i)+"\t"+time.strftime("%Y-%m-%d", time.localtime())+"\t"+str(random.randint(0,100000))+"\t"+str(random.randint(0,1000000))+"\t"+str(random.random())+"\t"+("fake%08d" % i) +"\t"+time.strftime("%Y-%m-%d", time.localtime())+ "\t" + str(random.randint(0,1000)) 

with open('test.txt', "wt") as f:
    for line in generate():
        f.write(line)
        f.write("\n")
