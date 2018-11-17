package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import springbook.user.domain.Level;
import springbook.user.domain.User;


public class UserTest {
	User user;
	
	@Before
	public void setUp() {
		user = new User();
	}
	
	// User에 추가한 upgradeLevel()메소드에 대한 테스트 추가 
	
	//User객체의 upgradeLevel() (레벨 업그레이드) 후에 다음 단계 레벨로 잘 바뀌는지 확인.
	@Test
	public void upgradeLevel(){
		Level[] levels = Level.values();
		for(Level level : levels){
			if(level.nextLevel() == null) continue;
			user.setLevel(level);
			user.upgradeLevel();
			assertThat(user.getLevel(), is(level.nextLevel()));
		}
	}
	
	//더 이상 업그레이드할 레벨이 없는 경우에 upgradeLevel()을 호출하면 예외가 발생하는지를 확인.
	@Test(expected=IllegalStateException.class)
	public void cannotUpgradeLevel(){
		Level[] levels = Level.values();
		for(Level level : levels){
			if(level.nextLevel() != null) continue;
			//nextLevel()이 null인 경우에 강제로 upgradeLevel()을 호출.
			user.setLevel(level);
			user.upgradeLevel();
		}
	}
}
