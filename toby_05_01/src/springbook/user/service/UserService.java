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
		for(User user : users){
			Boolean changed = null; //레벨의 변화가 있는지를 확인하는 flag
			if(user.getLevel() == Level.BASIC && user.getLogin() >= 50){ //50회 이상 로그인을 하면 BASIC에서 SILVER 레벨이 된다.
				user.setLevel(Level.SILVER);
				changed = true;
			}else if(user.getLevel() == Level.SILVER && user.getRecommend() >= 30){ //SILVER 레벨이면서 30번 이상 추천을 받으면 GOLD레벨이 된다.
				user.setLevel(Level.GOLD);
				changed = true;
			}else if(user.getLevel() == Level.GOLD){ changed = false; } //GOLD레벨은 변경 일어나지 않음.
			else{ changed = false; }

			if(changed){ userDao.update(user); } //레벨의 변경이 있는 경우에만 update()호출 
		}
	}
	
	//사용자 신규 등록 로직을 담은 add() 메소드 
	public void add(User user) {
		if(user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}
}
