package springbook.user.service;

import java.util.List;

import springbook.user.domain.User;

public interface UserService {
	public void add(User user);
	//new :: 메소드 신규 추가
	User get(String id);
	List<User> getAll();
	void deleteAll();
	void update(User user);
	
	public void upgradeLevels();
}
