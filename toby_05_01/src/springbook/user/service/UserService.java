package springbook.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

//사용자 관리 비즈니스 로직을 담을 클래스 
public class UserService {
	//테스트와 애플리케이션 코드에 중복되는 상수 값(로그인 횟수, 추천 수)을 정수형 상수로 변경. 
	//--> 업그레이드 조건 값이 바뀌는 경우 UserService의 상수 값만 변경해주면 됨.
//	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
//	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	UserDao userDao;
	//new
	UserLevelUpgradePolicy userLevelUpgradePolicy;
	
	//UserDao오브젝트의 DI가 가능하도록 수정자 메소드 추가.
	public UserDao getUserDao() {
		return userDao;
	}
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	public UserLevelUpgradePolicy getUserLevelUpgradePolicy() {
		return userLevelUpgradePolicy;
	}
	public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
		this.userLevelUpgradePolicy = userLevelUpgradePolicy;
	}
	
	//사용자 레벨 업그레이드 
	public void upgradeLevels() {
		//기본 작업 흐름만 남겨둔 upgradeLevels()
		//모든 사용자 정보를 가져와 한 명씩 업그레이드가 가능한지 확인하고, 가능하면 업그레이드를 한다.
		List<User> users = userDao.getAll();
		for(User user : users){
			//new 
			if(userLevelUpgradePolicy.canUpgradeLevel(user)){
				userLevelUpgradePolicy.upgradeLevel(user);
			}
		}
		
	}

//old
//	//업그레이드 가능 여부 확인 메소드 
//	private boolean canUpgradeLevel(User user){
//		Level currentLevel = user.getLevel();
//		//레벨별로 구분해서 조건을 판단한다.
//		switch(currentLevel) {
//			case BASIC : return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
//			case SILVER : return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
//			case GOLD : return false;
//			//현재 로직에서 다룰 수 없는 레벨이 주어지면 예외를 발생시킴. 새로운 레벨이 추가되고 로직을 수정하지 않으면 에러가 나서 확인할 수 있다. 
//			default : throw new IllegalArgumentException("Unknown Level: " + currentLevel);
//		}
//	}
//	
//	//레벨 업그레이드 작업 메소드
//	// 사용자 오브젝트의 레벨정보를 다음 단계로 변경하고, 변경된 오브젝트를 DB에 업데이트하는 두 가지 작업을 수행.
//	private void upgradeLevel(User user){
//		//사용자 정보가 바뀌는 부분을 UserService 메소드에서 User로 이동.
//		//UserService는 일일이 레벨 업그레이드 시에 User의 어떤 필드를 수정한다는 로직을 갖고 있기보다는, 
//		//User오브젝트에게 알아서 업그레이드에 필요한 작업을 수행하라고 요청만 해준다.
//		user.upgradeLevel();
//		userDao.update(user);
//	}
	
	//사용자 신규 등록 로직을 담은 add() 메소드 
	public void add(User user) {
		if(user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}
}
