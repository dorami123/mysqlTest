-- test1
-- +--------+-------------+------+-----+---------+-------+
-- | Field  | Type        | Null | Key | Default | Extra |
-- +--------+-------------+------+-----+---------+-------+
-- | name   | varchar(20) | YES  |     | NULL    |       |
-- | birth  | date        | YES  |     | NULL    |       |
-- | id     | int(11)     | NO   |     | NULL    |       |
-- | height | int(11)     | YES  |     | NULL    |       |
-- | weight | int(11)     | YES  |     | NULL    |       |
-- +--------+-------------+------+-----+---------+-------+
-- test2
-- +--------+-------------+------+-----+---------+----------------+
-- | Field  | Type        | Null | Key | Default | Extra          |
-- +--------+-------------+------+-----+---------+----------------+
-- | name   | varchar(20) | YES  | MUL | NULL    |                |
-- | birth  | date        | YES  |     | NULL    |                |
-- | id     | int(11)     | NO   | MUL | NULL    | auto_increment |
-- | height | int(11)     | YES  |     | NULL    |                |
-- | weight | int(11)     | YES  |     | NULL    |                |
-- +--------+-------------+------+-----+---------+----------------+
-- test3
-- +--------+-------------+------+-----+---------+----------------+
-- | Field  | Type        | Null | Key | Default | Extra          |
-- +--------+-------------+------+-----+---------+----------------+
-- | name   | varchar(20) | YES  | MUL | NULL    |                |
-- | birth  | date        | YES  |     | NULL    |                |
-- | id     | int(11)     | NO   | PRI | NULL    | auto_increment |
-- | height | int(11)     | YES  |     | NULL    |                |
-- | weight | int(11)     | YES  |     | NULL    |                |
-- +--------+-------------+------+-----+---------+----------------+
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