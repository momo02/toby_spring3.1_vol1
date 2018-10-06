-- users 테이블 생성
create table users (
	id varchar(10) primary key,
	name varchar(20) not null,
	password varchar(10) not null
)

SELECT * FROM users;

