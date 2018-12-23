-- users 테이블 생성
create table users (
	id varchar(10) primary key,
	name varchar(20) not null,
	password varchar(10) not null
)

SELECT * FROM users;

-- 기존의 users 테이블에  level, login, recommend 필드 추가
ALTER TABLE users ADD level tinyint NOT NULL; 
ALTER TABLE users ADD login int NOT NULL; 
ALTER TABLE users ADD recommend int NOT NULL; 
-- 확인 
desc users

-- 기존의 users 테이블에  email 필드 추가
ALTER TABLE users ADD email varchar(20); 
-- 확인 
desc users