package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

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
	
	List<User> users; //테스트 픽스처 
	
	@Before
	public void setUp() {
		users = Arrays.asList( //배열을 리스트로 만들어주는 편리한 메소드. 배열을 가변인자로 넣어주면 더욱 편리하다.
// old				
//		                 new User("junsung", "김준성", "p1", Level.BASIC, 49, 0)
//		                ,new User("goonyou", "공유", "p2", Level.BASIC, 50, 0)
//		                ,new User("yoon", "윤균상", "p3", Level.SILVER, 60, 29)
//		                ,new User("jenny", "제니", "p4", Level.SILVER, 60, 30)
//		                ,new User("park", "박성웅", "p5", Level.GOLD, 100, 100)
						
						//new -> UserService에 정의해둔 상수를 사용. 숫자로만 되어있는 위 경우와 달리 무슨 의도로 어떤 값을 넣었는지 이해하기 쉬워짐.
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
	public void upgradeLevels() {
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		userService.upgradeLevels();
/*old -> Level이 갖고 있어야 할 다음 레벨이 무엇인가 하는 정보를 테스트에 직접 넣어둘 이유가 없음.
		 레벨이 추가되거나 변경되면 테스트도 따라서 수정해 하니 번거로움.
		 또한 테스트 로직이 분명하게 드러나지 않음
*/		
//		//각 사용자별로 업그레이드 후의 예상 레벨을 검증.
//		checkLevel(users.get(0), Level.BASIC);
//		checkLevel(users.get(1), Level.SILVER);
//		checkLevel(users.get(2), Level.SILVER);
//		checkLevel(users.get(3), Level.GOLD);
//		checkLevel(users.get(4), Level.GOLD);
	
		//new 
		//각 사용자에 대해 업그레이드를 확인하려는 것인지 아닌지가 좀 더 이해하기 쉽게 true, false로 나타나 있어서 보기 좋음.
		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);
	}
	
//old	
//	//DB에서 사용자 정보를 가져와 레벨을 확인하는 코드가 중복되므로 헬퍼 메소드로 분리
//	private void checkLevel(User user, Level expectedLevel){
//		User userUpdate = userDao.get(user.getId());
//		assertThat(userUpdate.getLevel(), is(expectedLevel));
//	}
	
	//new -> 다음 레벨로 업그레이드 되거나, 안되는 것을 확인
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
}

