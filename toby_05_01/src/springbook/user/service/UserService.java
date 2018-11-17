package springbook.user.service;

import java.util.List;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

//사용자 관리 비즈니스 로직을 담을 클래스 
public class UserService {
	UserDao userDao;
	
	//UserDao오브젝트의 DI가 가능하도록 수정자 메소드 추가.
	public UserDao getUserDao() {
		return userDao;
	}
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	//사용자 레벨 업그레이드 
	public void upgradeLevels() {
		List<User> users = userDao.getAll();
// old
//		for(User user : users){
//			Boolean changed = null; //레벨의 변화가 있는지를 확인하는 flag
//			if(user.getLevel() == Level.BASIC && user.getLogin() >= 50){ //50회 이상 로그인을 하면 BASIC에서 SILVER 레벨이 된다.
//				user.setLevel(Level.SILVER);
//				changed = true;
//			}else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30){ //SILVER 레벨이면서 30번 이상 추천을 받으면 GOLD레벨이 된다.
//				user.setLevel(Level.GOLD);
//				changed = true;
//			}else if(user.getLevel() == Level.GOLD){ changed = false; } //GOLD레벨은 변경 일어나지 않음.
//			else{ changed = false; }
//
//			if(changed){ userDao.update(user); } //레벨의 변경이 있는 경우에만 update()호출 
//		}
	
		//new -> 기본 작업 흐름만 남겨둔 upgradeLevels()
		// 모든 사용자 정보를 가져와 한 명씩 업그레이드가 가능한지 확인하고, 가능하면 업그레이드를 한다.
		for(User user : users){
			if(canUpgradeLevel(user)){
				upgradeLevel(user);
			}
		}
		
	}
	
	//업그레이드 가능 여부 확인 메소드 
	private boolean canUpgradeLevel(User user){
		Level currentLevel = user.getLevel();
		//레벨별로 구분해서 조건을 판단한다.
		switch(currentLevel) {
			case BASIC : return (user.getLogin() >= 50);
			case SILVER : return (user.getRecommend() >= 30);
			case GOLD : return false;
			//현재 로직에서 다룰 수 없는 레벨이 주어지면 예외를 발생시킴. 새로운 레벨이 추가되고 로직을 수정하지 않으면 에러가 나서 확인할 수 있다. 
			default : throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		}
	}
	
	//레벨 업그레이드 작업 메소드
	// 사용자 오브젝트의 레벨정보를 다음 단계로 변경하고, 변경된 오브젝트를 DB에 업데이트하는 두 가지 작업을 수행.
	private void upgradeLevel(User user){
		if(user.getLevel() == Level.BASIC) user.setLevel(Level.SILVER);
		else if(user.getLevel() == Level.SILVER) user.setLevel(Level.GOLD);
		userDao.update(user);
	}
	
	//사용자 신규 등록 로직을 담은 add() 메소드 
	public void add(User user) {
		if(user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}
}
