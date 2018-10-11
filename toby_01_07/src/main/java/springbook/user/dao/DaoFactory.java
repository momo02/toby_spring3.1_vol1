package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정정보라는 표시
public class DaoFactory {
	//old
//	@Bean 
//	public UserDao userDao(){
//		return new UserDao(connectionMaker());
//	}
	//new
	@Bean 
	public UserDao userDao(){
		return new UserDao();
	}
	@Bean
	public ConnectionMaker connectionMaker(){
		return new DConnectionMaker(); 
	}
	
}
