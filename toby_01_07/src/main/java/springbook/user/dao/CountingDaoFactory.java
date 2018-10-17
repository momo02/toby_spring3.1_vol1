package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //애플리케이션 컨텍스트 또는 빈 팩토리가 사용할 설정정보라는 표시
public class CountingDaoFactory {
	@Bean 
	public UserDao userDao(){
		return new UserDao(connectionMaker()); //모든 DAO는 여전히 connectionMaker()에서 만들어지는 오브젝트를 DI받는다.
	}
	@Bean
	public ConnectionMaker connectionMaker(){
		return new CountingConnectionMaker(realConnectionMaker()); 
	}
	@Bean 
	public ConnectionMaker realConnectionMaker(){
		return new DConnectionMaker();
	}
}
