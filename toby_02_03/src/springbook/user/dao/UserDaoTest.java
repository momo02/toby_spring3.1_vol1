package springbook.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import springbook.user.domain.User;

// JUnit 프레임워크에서 동작할 수 있는 테스트 메소드로 전환
public class UserDaoTest {
	
	public static void main(String[] org){
		//JUnit 테스트 실행
		//메소드 파라미터에는 @Test 테스트 메소드를 가진 클래스의 이름을 넣어줌
		JUnitCore.main("springbook.user.dao.UserDaoTest");
	}
	
	@Test //JUnit에게 테스트용 메소드임을 알려줌.
	//JUnit테스트 메소드는 반드시 public으로 선언.
	public void addAndGet() throws SQLException, ClassNotFoundException {
		
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		
		UserDao dao = context.getBean("userDao",UserDao.class); 
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		User user = new User();
		user.setId("gongyou");
		user.setName("공유");
		user.setPassword("1234");
		
		dao.add(user);
		assertThat(dao.getCount(), is(1));
		
		User user2 = dao.get(user.getId());
	
		assertThat(user2.getName(), is(user.getName()));
		assertThat(user2.getPassword(), is(user.getPassword()));
	}
	//getCount() 테스트
	@Test
	public void count() throws SQLException, ClassNotFoundException {
		
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		
		UserDao dao = context.getBean("userDao",UserDao.class); 
		User user1 = new User("user1","유저일","springno1");
		User user2 = new User("user2","유저이","springno2");
		User user3 = new User("user3","유저삼","springno3");
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
	}
}
