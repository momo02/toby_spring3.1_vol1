package springbook.user.dao;

import java.util.ArrayList;
import java.util.List;

import springbook.user.domain.User;

//6.2 고립된 단위 테스트
//6.2.2 테스트 대상 오브젝트 고립시키기 
//upgradeLevels() 테스트에서 실제 UserDao와 DB에 의존하지않도록 'UserDao 목 오브젝트'를 만들어 적용.
public class MockUserDao implements UserDao{
	private List<User> users; //레벨 업그레이드 후보 User 오브젝트 목록
	private List<User> updated = new ArrayList<User>(); //업그레이드 대상 오브젝트를 저장해둘 목록 
	 
	public MockUserDao(List<User> users) {
		this.users = users;
	}
	
	public List<User> getUpdated() {
		return this.updated;
	}
	
	//스텁 기능 제공 
	// -> 생성자를 통해 전달받은 사용자 목록을 저장해뒀다가, getAll()메소드가 호출되면 DB에서 가져온것처럼 돌려주는 용도.
	public List<User> getAll() {
		return this.users;
	}
	
	//목 오브젝트 기능 제공
	// -> update()메소드를 실행하면서 넘겨준 업그레이드 대상 User 오브젝트를 저장해뒀다가 검증을 위해 돌려주기 위한 것.
	public void update(User user) {
		this.updated.add(user);
	}
	
	//테스트에 사용되지 않는 메소드 (지원하지않는 기능이라는 예외가 발생하도록 함)
	public void add(User user) { throw new UnsupportedOperationException(); }
	public void deleteAll() { throw new UnsupportedOperationException(); }
	public User get(String id) { throw new UnsupportedOperationException(); }
	public int getCount() { throw new UnsupportedOperationException(); }

}
