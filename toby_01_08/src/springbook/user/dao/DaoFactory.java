package springbook.user.dao;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration //애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정정보라는 표시
public class DaoFactory {
	@Bean //수정자 메소드 DI를 사용하는 팩토리 메소드
	public UserDao userDao(){
		UserDao userDao = new UserDao();
		userDao.setDataSource(dataSource());
		return userDao;
	}

	@Bean
	public DataSource dataSource(){
		//DB 연결정보를 수정자메소드를 통해 넣어줌 -> 오브젝트 레벨에서 DB 연결 방식 변경 가능.
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		
		dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mysql://localhost:3306/toby_spring?characterEncoding=UTF-8");
		dataSource.setUsername("root");
		dataSource.setUsername("1234");
		
		return dataSource; 
	}
}
