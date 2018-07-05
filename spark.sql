txt or csv文件导入数据仓库
create table yingxiao_source_data(deviceid string) row format serde 'org.apache.hadoop.hive.serde2.OpenCSVSerde' with SERDEPROPERTIES ("separatorChar"=",") STORED AS TEXTFILE;

load data local inpath '/home/hadoop/yunsi/yingxiao/imei1-150w.txt' into table yingxiao_source_data;

insert overwrite table yingxiao_data partition(package) select deviceid,'idfa','package4' from yingxiao_source_data;

数据仓库数据推送到mysql
CREATE OR REPLACE TEMPORARY VIEW mysql_db_name
USING org.apache.spark.sql.jdbc
OPTIONS (
  url 'jdbc:mysql://url.com:3306/db_name?characterEncoding=utf8&useServerPrepStmts=false&rewriteBatchedStatements=true',
  dbtable 'table_name',
  driver 'com.mysql.jdbc.Driver',
  user 'username',
  password 'password'
);
insert into table mysql_table_name
select * from table_name
where 条件;