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
		
		User user = new User();
		user.setId("gongyou");
		user.setName("공유");
		user.setPassword("1234");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공");
		
		User user2 = dao.get(user.getId());
		
		//old
//		if(!user.getName().equals(user2.getName())){
//			System.out.println("테스트 실패 (name)");
//		}
//		else if(!user.getPassword().equals(user2.getPassword())){
//			System.out.println("테스트 실패 (password)");
//		}
//		else{
//			System.out.println(user2.getId() + " 조회 성공");
//		}
		
		//new
		assertThat(user2.getName(), is(user.getName()));
		assertThat(user2.getPassword(), is(user.getPassword()));
		
		/* 	assertThat()메소드는 첫 번째 파라미터의 값을 뒤에 나오는 matcher라고 불리는 조건으로 비교해서
			일치하면 다음으로 넘어가고, 아니면 테스트가 실패하도록 함.
			is()는 매처의 일종으로 equals()로 비교해주는 기능을 가짐. */
		
	}
}
