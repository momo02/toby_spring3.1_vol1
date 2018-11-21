package springbook.user.service;

import springbook.user.domain.User;
/*
 * 사용자 레벨 업그레이드 정책을 유연하게 변경할 수 있도록 개선
 */
// 업그레이드 정책 인터페이스 
public interface UserLevelUpgradePolicy {
	boolean canUpgradeLevel(User user);
	void upgradeLevel(User user);
	int getMinLogcountForSilver();
	int getMinReccomendForGold();
}
