package springbook.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

// JUnit 프레임워크에서 동작할 수 있는 테스트 메소드로 전환
public class UserDaoTest {
	private UserDao dao; //setUp()메소드에서 만드는 오브젝트를 테스트 메소드에서 사용할 수 있도록 인스턴스 변수로 선언.
	private User user1;
	private User user2;
	private User user3;
	
	@Before //JUnit 제공 어노테이션. @Test 메소드가 실행되기 전에 먼저 실행돼야하는 메소드를 정의.
	public void setUp(){
		ApplicationContext context = new GenericXmlApplicationContext("applicationContext.xml");
		dao = context.getBean("userDao",UserDao.class); 
		
		this.user1 = new User("user1","유저일","springno1");
		this.user2 = new User("user2","유저이","springno2");
		this.user3 = new User("user3","유저삼","springno3");
	}
	
	@Test //JUnit에게 테스트용 메소드임을 알려줌.
	//JUnit테스트 메소드는 반드시 public으로 선언.
	public void addAndGet() throws SQLException, ClassNotFoundException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		User userget1 = dao.get(user1.getId());
		assertThat(userget1.getName(), is(user1.getName()));
		assertThat(userget1.getPassword(), is(user1.getPassword()));
		
		User userget2 = dao.get(user2.getId());
		assertThat(userget2.getName(), is(user2.getName()));
		assertThat(userget2.getPassword(), is(user2.getPassword()));
	}
	
	//getCount() 테스트
	@Test
	public void count() throws SQLException, ClassNotFoundException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
	}
	
	//get() 예외조건에 대한 테스트 (전달된 id값에 해당하는 사용자 정보가 없을 경우)
	@Test(expected=EmptyResultDataAccessException.class) //테스트 중에 발생할 것으로 기대하는 예외 클래스를 지정해줌.
	public void getUserFailure() throws SQLException, ClassNotFoundException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		//이 메소드 실행 중에 예외가 발생해야 한다.
		//예외가 발생하지 않으면 테스트가 실패한다.
		dao.get("unknown_id");
	}
}
