package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.MockUserDao;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/test-applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserService userService; 
	@Autowired
	UserServiceImpl userServiceImpl;
	@Autowired
	UserDao userDao;
	@Autowired
	PlatformTransactionManager transactionManager;
	@Autowired
	MailSender mailSender;
	//팩토리 빈을 가져오려면 애플리케이션 컨텍스트가 필요.
	@Autowired 
	ApplicationContext context;
	
	List<User> users; //테스트 픽스처 
	
	//테스트용 예외
	static class TestUserServiceException extends RuntimeException {
	}
	
	//UserService의 테스트용 대역 클래스
	// (upgradeLevels메소드에서 모든 사용자에 대해 업그레이드 작업을 진행하다,
	//  중간에 예외가 발생하여 작업이 중단되는 경우를 테스트)
	static class TestUserService extends UserServiceImpl { 
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
	
	/*
	 * cf. 테스트 대역 : 테스트 대상이 사용하는 의존 오브젝트를 대체할 수 있도록 만든 오브젝트.
	 * 테스트 대역 중에서 테스트 대상으로부터 전달받은 정보를 검증할 수 있도록 설계된 것을 '목 오브젝트'라고 함. 
	 */
	//목 오브젝트로 만든 메일 전송 확인용 클래스
	//기능 	1. UserService의 코드가 정상적으로 수행되도록 돕는 역할  
	//	    2. 테스트 대상이 넘겨주는 출력 값을 보관해두는 기능을 추가
	static class MockMailSender implements MailSender {
		//UserService로부터 전송 요청을 받은 메일 주소를 저장해두고 이를 읽을 수 있게 함.
		private List<String> requests = new ArrayList<String>();
		
		public List<String> getRequest() {
			return requests;
		}
	
		public void send(SimpleMailMessage mailMessage) throws MailException {
			requests.add(mailMessage.getTo()[0]); // 전송 요청을 받은 이메일 주소를 저장해둔다. 
			//한 번에 한 명씩 보내기 때문에, 첫 번째 수신자 메일 주소를 미리 준비해둔 리스트에 저장.
		}

		public void send(SimpleMailMessage[] arg0) throws MailException {
			
		}

	}
	
	@Before
	public void setUp() {
		users = Arrays.asList( //배열을 리스트로 만들어주는 편리한 메소드. 배열을 가변인자로 넣어주면 더욱 편리하다.
						//UserService에 정의해둔 상수를 사용. 숫자로만 되어있는 위 경우와 달리 무슨 의도로 어떤 값을 넣었는지 이해하기 쉬워짐.
						 new User("junsung", "김준성", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "chunkind@naver.com") //cf. 테스트에서는 가능한 한 경계 값을 사용하는 것이 좋다.
						,new User("gongyu", "공유", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "gongyu@gamil.com")
						,new User("yoon", "윤균상", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1, "yoon@gamil.com")
						,new User("jenny", "제니", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "jenny@gamil.com")
						,new User("park", "박성웅", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "parksy@daum.net")
				);
	}
	
	//userService 빈의 주입을 확인하는 테스트
	@Test
	public void bean() {
		assertThat(this.userService, is(notNullValue()));
	}
	
	//new :: MockUserDao를 사용해서 만든 고립된 테스트
	//사용자 레벨 업그레이드 + 메일 발송 대상을 확인하는 테스트 
	@Test
	public void upgradeLevels() throws Exception {
		//고립된 테스트에서는 테스트 대상 오브젝트를 직접 생성.
		//--> 컨테이너에서 가져온 UserService 오브젝트는 DI를 통해서 많은 의존 오브젝트와 서비스, 외부 환경에 의존.
		//    이제는 완전히 고립돼서 테스트만을 위해 독립적으로 동작하는 테스트 대상을 사용할 것이기 때문에 스프링 컨테이너에서 빈을 가져올 필요가 없음.
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		//목 오브젝트로 만든 UserDao를 직접 DI
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		//메일 발송 결과를 테스트할 수 있도록 목 오브젝트를 만들어 userService의 의존 오브젝트로 주입
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels(); //-> 업그레이드 테스트, 메일 발송이 일어나면 MockMailSender 오브젝트의 requests 리스트에 그 결과가 저장됨 
		
		//MockUserDao로부터 업데이트 결과를 가져와 업데이트 횟수와 정보를 확인.
		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0),"gongyu", Level.SILVER);
		checkUserAndLevel(updated.get(1),"jenny", Level.GOLD);
		
		//목 오브젝트에 저장된 메일 수신자 목록을 가져와 업그레이드 대상과 일치하는지 확인.
		List<String> request = mockMailSender.getRequest();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
	}
	
	//위의 upgradeLevels 메소드에 목 프레임워크 Mockito를 적용한 테스트 코드
	@Test 
	public void mockUpgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		//cf. Mockito와 같은 목 프레임워크의 특징은 목 클래스를 일일히 준비해둘 필요가 없다. 
		//	  간단한 메소드 호출만으로 다이나믹하게 특정 인터페이스를 구현한 테스트용 목 오브젝트를 만들 수 있다. 
		UserDao mockUserDao = mock(UserDao.class);
		//getAll 메소드가 호출됐을 때, users리스트를 리턴해주라는 선언.
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		//User타입의 오브젝트를 파라미터로 받으며 update()메소드가 두 번 호출됬는지 ( times(2) ) 확인하라(verify) 는 것. 
		//any()를 사용하면 파라미터 내용은 무시하고 호출 횟수만 확인할 수 있다. 
		verify(mockUserDao, times(2)).update(any(User.class));
		//users.get(1)을 파라미터로 update()가 호출된 적이 있는지를 확인.
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
		//파라미터를 정밀하게 검사하기 위해 캡쳐할 수도 있다 
		//파라미터를 직접 비교하기보다는 파라미터의 내부 정보를 확인해야하는 경우에 유용.
		verify(mockMailSender, times(2)).send(mailMessageArg.capture()); 
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
	}
	
	//id와 level을 확인하는 헬퍼 메소드
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel){
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
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
	@DirtiesContext //컨텍스트 무효화 애노테이션
	public void upgradeAllOrNothing() throws Exception {
		//예외를 발생시킬 4번째 사용자의 id를 넣어서 테스트용 UserService대역 오브젝트를 생성. 
		UserServiceImpl testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao); //UserDao를 수동 DI 
		testUserService.setMailSender(mailSender);

		//팩토리 빈 자체를 가져와야 하므로 빈 이름에 &를 반드시 넣어야 한다.
		TxProcyFactoryBean txProxyFactoryBean = 
		context.getBean("&userService", TxProcyFactoryBean.class); 
		txProxyFactoryBean.setTarget(testUserService); //테스트용 타깃 주입
		//변경된 타깃 설정을 이용해서 트랜잭션 다이내믹 프록시 오브젝트를 다시 생성.
		UserService txUserService = (UserService) txProxyFactoryBean.getObject();
		
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try{
			//TestUserService는 업그레이드 작업 중에 예외가 발생해야 함. 
			txUserService.upgradeLevels(); 
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

