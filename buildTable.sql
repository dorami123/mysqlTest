-- key [mul,uni,pri]
create database if not exists test;
use test;  
-- 无索引表
create table if not exists test1(   
name varchar(20),
birth date,
id int NOT NULL,
height int,
weight int
)engine = MyISAM;
-- )engine = MyISAM;  

-- 有索引表
create table if not exists test2(   
name varchar(20),
birth date,
id int NOT NULL auto_increment,
height int,
weight int,
index (name),
index (id)
)engine = MyISAM;
-- )engine = MyISAM; 

-- 有唯一索引表
create table if not exists test3(   
name varchar(20),
birth date,
id int NOT NULL UNIQUE auto_increment,
height int,
weight int,
index (name)
)engine = MyISAM;
-- )engine = MyISAM;

-- SELECT * INTO OUTFILE '/mnt/mysqlTest/data.txt'
-- FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
-- LINES TERMINATED BY '\n'
-- FROM test1;

-- ALTER TABLE ... DISABLE KEYS
-- ALTER TABLE ... ENABLE KEYS 