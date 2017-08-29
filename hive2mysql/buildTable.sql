-- hive
CREATE DATABASE IF NOT EXISTS zhaoxiao
COMMENT 'hivereadertest'
location 'hdfs://testcluster/user/gzzhaoxiao/warehouse2/';


CREATE DATABASE IF NOT EXISTS zhaoxiao
COMMENT 'hivereadertest'
location 'hdfs://testcluster/user/gzzhaoxiao/warehouse2/';


create table if not exists test1(
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

 LOAD DATA LOCAL INPATH 't.txt' INTO TABLE test1;


--mysql

CREATE TABLE if not exists test1(  
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



 LOAD DATA INFILE 'test.txt' INTO TABLE test2
  FIELDS TERMINATED BY '\t';

 grant all privileges on gzwangweiyi01.* to gzwangweiyi01@gdc-nn01-testing.i.nease.net;
flush privileges;



