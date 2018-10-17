package springbook.user.dao;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import springbook.user.domain.User;

public class UserDaoConnectionCountingTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		ApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
		
		UserDao dao = context.getBean("userDao",UserDao.class); 
		
		User user = new User();   
		user.setId("momo02");     
		user.setName("모모");       
		user.setPassword("1234"); 
		
		dao.add(user);
		
		User user1 = new User();   
		user1.setId("bobo");     
		user1.setName("보보");       
		user1.setPassword("1234"); 
		
		dao.add(user1);
		
		dao.get("bobo");
		
		//DL(의존관계 검색)을 사용하면 이름을 이용해 어떤 빈이든 가져올 수 있다. 
		CountingConnectionMaker ccm = context.getBean("connectionMaker",CountingConnectionMaker.class);
		System.out.println("Connection counter : " + ccm.getCounter());
	}
}
