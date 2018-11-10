package springbook.user.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.domain.User;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserDaoTest {
	@Autowired
	private UserDao dao; //setUp()메소드에서 만드는 오브젝트를 테스트 메소드에서 사용할 수 있도록 인스턴스 변수로 선언.
	
	private User user1;
	private User user2;
	private User user3;
	
	@Before //JUnit 제공 어노테이션. @Test 메소드가 실행되기 전에 먼저 실행돼야하는 메소드를 정의.
	public void setUp(){
//		//오브젝트 생성, 관계설정 등을 모두 직접 해준다.
//		dao = new UserDao();
//		DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost:3306/testdb?characterEncoding=UTF-8","root","1234",true);
//		dao.setDataSource(dataSource);
//		
//		//JdbcContext 객체 생성 및 관계설정 추가
//		JdbcContext jdbcContext = new JdbcContext();
//		jdbcContext.setDataSource(dataSource);
//		dao.setJdbcContext(jdbcContext);
		
		this.user1 = new User("gyumee","유저일","springno1");
		this.user2 = new User("leegw700","유저이","springno2");
		this.user3 = new User("bumjin","유저삼","springno3");
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
	
	//getAll()에 대한 테스트
	/* -> getAll()메소드는 현재 등록되어 있는 모든 사용자 정보를 가져온다.
	 *    기본키인 id순으로 정렬해서 가져오도록 함.
	 */    
	@Test
	public void getAll() throws SQLException, ClassNotFoundException {
		dao.deleteAll();
	
		//데이터가 없는 경우에 대한 검증 코드 추가. 
		//cf.query()는 결과가 없을 경우에 크기가 0인 List<T> 오브젝트를 리턴. getAll()은 query()가 돌려주는 결과를 그대로 리턴하도록 함. 
		List<User> users0 = dao.getAll(); 
		assertThat(users0.size(), is(0));
		
		//cf. getAll => 현재 등록되어있는 
		dao.add(user1); //Id : gyumee
		List<User> users1 = dao.getAll();
		assertThat(users1.size(), is(1));
		checkSameUser(user1,users1.get(0));
		
		dao.add(user2); //Id : leegw700
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(2));
		checkSameUser(user1,users2.get(0));
		checkSameUser(user2,users2.get(1));
		
		
		dao.add(user3); //Id : bumjin
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(3));
		checkSameUser(user3,users3.get(0)); //user3의 id값이 알바벳순으로 가장 빠르므로 getAll()의 첫번째 엘리멘트여야 함.
		checkSameUser(user1,users3.get(1));
		checkSameUser(user2,users3.get(2));
		
	}
	
	//User오브젝트의 내용을 비교하는 검증 코드. 테스트에서 반복적으로 사용되므로 분리.
	private void checkSameUser(User user1, User user2){
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
	}
}
