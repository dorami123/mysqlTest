-- key [mul,uni,pri]
use test;  
-- 无索引表
create table if not exists test1(   
name varchar(20),
birth date,
id int NOT NULL,
height int,
weight int
);  

-- 有索引表
create table if not exists test2(   
name varchar(20),
birth date,
id int NOT NULL,
height int,
weight int,
index indexname(id)
); 

-- 有唯一索引表
create table if not exists test3(   
name varchar(20),
birth date,
id int UNIQUE,
height int,
weight int
);

-- SELECT * INTO OUTFILE '/mnt/mysqlTest/data.txt'
-- FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
-- LINES TERMINATED BY '\n'
-- FROM test1;

-- ALTER TABLE ... DISABLE KEYS
-- ALTER TABLE ... ENABLE KEYS 