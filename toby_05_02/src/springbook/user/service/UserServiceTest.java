package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import static springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECCOMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserService userService; 
	@Autowired
	UserDao userDao;
	@Autowired
	DataSource dataSource;
	
	List<User> users; //테스트 픽스처 
	
	//테스트용 예외
	static class TestUserServiceException extends RuntimeException {
	}
	
	//UserService의 테스트용 대역 클래스
	// (upgradeLevels메소드에서 모든 사용자에 대해 업그레이드 작업을 진행하다,
	//  중간에 예외가 발생하여 작업이 중단되는 경우를 테스트)
	static class TestUserService extends UserService { 
		private String id;
		
		private TestUserService(String id){ //예외를 발생시킬 User오브젝트의 id를 지정할 수 있게 만든다.
			this.id = id;
		}
		
		protected void upgradeLevel(User user){ //UserService의 메소드를 오버라이드
			//지정된 id의 User오브젝트가 발견되면 예외를 던져서 작업을 강제로 중단시킨다.
			if(user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}
	
	@Before
	public void setUp() {
		users = Arrays.asList( //배열을 리스트로 만들어주는 편리한 메소드. 배열을 가변인자로 넣어주면 더욱 편리하다.
						//UserService에 정의해둔 상수를 사용. 숫자로만 되어있는 위 경우와 달리 무슨 의도로 어떤 값을 넣었는지 이해하기 쉬워짐.
						 new User("junsung", "김준성", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0) //cf. 테스트에서는 가능한 한 경계 값을 사용하는 것이 좋다.
						,new User("goonyou", "공유", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0)
						,new User("yoon", "윤균상", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1)
						,new User("jenny", "제니", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD)
						,new User("park", "박성웅", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
				);
	}
	
	//userService 빈의 주입을 확인하는 테스트
	@Test
	public void bean() {
		assertThat(this.userService, is(notNullValue()));
	}
	
	//사용자 레벨 업그레이드 테스트 
	@Test
	public void upgradeLevels() throws Exception {
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		userService.upgradeLevels();

		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);
	}
	
	//다음 레벨로 업그레이드 되거나, 안되는 것을 확인
	//(boolean upgraded > 어떤 레벨로 바뀔 것인가가 아니라, 다음 레벨로 업그레이드될 것인가 아닌가를 지정)
	private void checkLevelUpgraded(User user, boolean upgraded){
		User userUpdate = userDao.get(user.getId());
		if(upgraded){
			//업그레이드가 일어났는지 확인					
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel())); //-> 다음 레벨이 무엇인지는 Level에게 물어보면 됨.
		}else{ //업그레이드가 일어나지 않았는지 확인
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}
	
	@Test
	public void add() {
		userDao.deleteAll();
		
		User userWithLevel = users.get(4); //GOLD 레벨 => GOLD 레벨이 이미 지정된 User라면 레벨 초기화 X.
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null); //레벨이 비어 있는 사용자. 로직에 따라 등록 중에 BASIC레벨로 설정돼야 함.
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		//cf. 레벨이 이미 설정됐던 것은 그대로 유지되고, 레벨이 없던 것은 디폴트인 BASIC으로 설정됐는지 확인.
		assertThat(userWithLevelRead.getLevel(),is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(),is(Level.BASIC));
	}
	
	//예외 발생 시 작업 취소 여부 테스트
	//-> 사용자 레벨 업그레이드를 시도하다 중간에 예외가 발생했을 경우, 그 전에 업그레이드했던 사용자도 다시 원상태로 돌아갔는지를 확인 
	@Test
	public void upgradeAllOrNothing() throws Exception {
		//예외를 발생시킬 4번째 사용자의 id를 넣어서 테스트용 UserService대역 오브젝트를 생성. 
		UserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao); //UserDao를 수동 DI 
		testUserService.setDataSource(this.dataSource); //트랜잭션 동기화에 필요한 DataSource를 수동 DI
		
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try{
			//TestUserService는 업그레이드 작업 중에 예외가 발생해야 함. 
			testUserService.upgradeLevels(); 
			fail("TestUserServiceException expected"); //정상 종료라면 문제가 있으니 실패
			//cf. 혹여나 테스트 코드를 잘못 작성해서 예외 발생 없이 upgradeLevels()메소드가 정상 종료되어도
			//    fail()메소드 때문에 테스트가 실패할 것.
			
		}catch(TestUserServiceException e){
			//TestUserServiceException가 던져주는 예외를 잡아서 계속 진행되도록 한다.
			//그 외의 예외라면 테스트 실패 
		}
		//예외가 발생하기 전에 레벨 변경이 있었던 사용자의 레벨이 처음 상태로 바뀌었나 확인 
		checkLevelUpgraded(users.get(1), false);
	}
	
}

