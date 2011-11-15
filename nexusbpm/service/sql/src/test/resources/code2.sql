create table sql_test_table (
	KEY_ decimal(22, 0) not null,
	NAME_ varchar(125) not null,
	constraint pk_sql_test_table primary key (KEY_)
	);

-- insert a row of data
insert into sql_test_table (KEY_, NAME_) values (1, 'Value number; 1');

-- insert another row of data;
insert into sql_test_table (KEY_, NAME_) values (2, 'another value''');

/* Insert ' a third value */
insert into sql_test_table (KEY_, NAME_) values (3, 'a third \' value');

-- insert a final value
insert into SQL_TEST_TABLE (KEY_, NAME_) values (4, 'value four');

select * from SQL_TEST_TABLE;

/* drop the table at the end to clean everything up */
drop table sql_test_table;
