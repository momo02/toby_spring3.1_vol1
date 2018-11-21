package springbook.user.service;

import org.springframework.beans.factory.annotation.Autowired;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

public class EventUserLevelUpgradePolicy implements UserLevelUpgradePolicy {

	private int minLogcountForSilver;
	private int minReccomendForGold;
	
	public int getMinLogcountForSilver() {
		return minLogcountForSilver;
	}

	public void setMinLogcountForSilver(int minLogcountForSilver) {
		this.minLogcountForSilver = minLogcountForSilver;
	}

	public int getMinReccomendForGold() {
		return minReccomendForGold;
	}

	public void setMinReccomendForGold(int minReccomendForGold) {
		this.minReccomendForGold = minReccomendForGold;
	}

	@Autowired
	UserDao userDao;
	
	public boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		//레벨별로 구분해서 조건을 판단한다.
		switch(currentLevel) {
			case BASIC : return (user.getLogin() >= minLogcountForSilver);
			case SILVER : return (user.getRecommend() >= minReccomendForGold);
			case GOLD : return false;
			//현재 로직에서 다룰 수 없는 레벨이 주어지면 예외를 발생시킴. 새로운 레벨이 추가되고 로직을 수정하지 않으면 에러가 나서 확인할 수 있다. 
			default : throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		}
	}

	public void upgradeLevel(User user) {
		System.out.println("이벤트 사용자 업그레이드 로직 수행");
		// .... 
		//이벤트 기간에 사용자 업그레이드 로직 수행 시 추가될 기타 로직을 넣어줌.
		// ....
		user.upgradeLevel();
		userDao.update(user);
	}

}
