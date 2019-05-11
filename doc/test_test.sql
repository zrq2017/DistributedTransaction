--schema test
create table if not exists test
(
	id int auto_increment
		primary key,
	name varchar(45) null
)
;

INSERT INTO test.test (id, name) VALUES (13, 'server1');
INSERT INTO test.test (id, name) VALUES (14, 'server2');
INSERT INTO test.test (id, name) VALUES (15, 'server1');
INSERT INTO test.test (id, name) VALUES (16, 'server2');