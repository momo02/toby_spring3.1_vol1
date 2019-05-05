package springbook.user.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import springbook.user.domain.User;

@Transactional
public interface UserService {
	// 아래 4개의 메소드는 메소드 레벨에 @Transactional 어노테이션이 없으므로 대체 정책에 따라 
	// 타입 레벨에 부여된 디폴트 속성이 적용됨
	public void add(User user);
	void deleteAll();
	void update(User user);
	public void upgradeLevels();
	
	@Transactional(readOnly=true)
	User get(String id);
	@Transactional(readOnly=true)
	List<User> getAll();
}
