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

CREATE TABLE if not exists test4(  
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,  
  `a` int(11) NOT NULL,  
  `b` bigint(20) unsigned NOT NULL,  
  `c` bigint(20) unsigned NOT NULL,  
  `d` int(10) unsigned NOT NULL,  
  `e` int(10) unsigned NOT NULL,  
  `f` int(10) unsigned NOT NULL,  
  PRIMARY KEY (`id`),  
  KEY `a_b` (`a`,`b`)  
) ENGINE=MyISAM;


CREATE TABLE if not exists test5(  
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,  
  `a` date NOT NULL,  
  `b` int unsigned NOT NULL,  
  `c` bigint unsigned NOT NULL,  
  `d` double unsigned NOT NULL,  
  `e` varchar(20) NOT NULL,  
  `f` TIMESTAMP NOT NULL,
  `g` blob NOT NULL , 
  PRIMARY KEY (`id`),  
  index `a` (`a`)  
) ENGINE=MyISAM;

CREATE TABLE if not exists test6(  
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,  
  `a` date NOT NULL,  
  `b` int unsigned NOT NULL,  
  `c` bigint unsigned NOT NULL,  
  `d` double unsigned NOT NULL,  
  `e` varchar(20) NOT NULL,  
  `f` TIMESTAMP NOT NULL,
  `g` blob NOT NULL , 
  PRIMARY KEY (`id`),  
  index `a` (`a`)  
) ENGINE=MyISAM;

CREATE TABLE if not exists test7(  
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,  
  `a` date NOT NULL,  
  `b` int unsigned NOT NULL,  
  `c` bigint unsigned NOT NULL,  
  `d` double unsigned NOT NULL,  
  `e` varchar(20) NOT NULL,  
  `f` TIMESTAMP NOT NULL,
  `g` blob NOT NULL , 
  PRIMARY KEY (`id`),  
  index `a` (`a`)  
) ENGINE=MyISAM;    




CREATE TABLE if not exists test8(  
  `a` date NOT NULL,  
  `b` int unsigned NOT NULL,  
  `c` bigint unsigned NOT NULL,  
  `d` double unsigned NOT NULL,  
  `e` varchar(20) NOT NULL,  
  `f` TIMESTAMP NOT NULL,
  `g` blob NOT NULL ,   
  index `a` (`a`)  
) ENGINE=MyISAM; 
-- SELECT * INTO OUTFILE '/mnt/mysqlTest/data.txt'
-- FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"'
-- LINES TERMINATED BY '\n'
-- FROM test1;

-- ALTER TABLE ... DISABLE KEYS
-- ALTER TABLE ... ENABLE KEYS 

CREATE TABLE if not exists test(  
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,  
  `a` date NOT NULL,  
  `b` int unsigned NOT NULL,  
  `c` bigint unsigned NOT NULL,  
  `d` double unsigned NOT NULL,  
  `e` varchar(20) NOT NULL,  
  `f` TIMESTAMP NOT NULL,
  `g` blob NOT NULL , 
  PRIMARY KEY (`id`),  
  index `a` (`a`)  
) ;


CREATE TABLE if not exists test2(  
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,  
  `a` date NOT NULL,  
  `b` int unsigned NOT NULL,  
  `c` bigint unsigned NOT NULL,  
  `d` double unsigned NOT NULL,  
  `e` varchar(20) NOT NULL,  
  `f` TIMESTAMP NOT NULL,
  `g` blob NOT NULL , 
  PRIMARY KEY (`id`),  
  index `a` (`a`)  
) engine=INNODB;

CREATE DATABASE IF NOT EXISTS zhaoxiao
COMMENT 'hivereadertest'
location 'hdfs://testcluster/user/gzzhaoxiao/warehouse2/';


create table if not exists test(
  id int,
  a date,
  b int,
  c bigint,
  d double,
  e string,
  f date,
  g string
)
 ROW FORMAT DELIMITED
   FIELDS TERMINATED BY '\t'
 STORED AS TEXTFILE;

 LOAD DATA INFILE 'test.txt' INTO TABLE test2
  FIELDS TERMINATED BY '\t';


 insert into test2 values(1,2017-08-18,1729,570704,0.139100298594,fake00000001,2017-08-18,407);