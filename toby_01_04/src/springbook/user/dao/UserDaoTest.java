package springbook.user.dao;

import java.sql.SQLException;

import springbook.user.domain.User;

// 관계설정 책임이 추가된 UserDao 클라이언트인 UserDaoTest의 main메소드 
// UserDaoTests는 UserDao와 ConnectionMaker구현 클래스와의 런타임 오브젝트 의존관계를 설정하는 책임을 담당.
public class UserDaoTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		
		//UserDaoTest는 이제 UserDao가 어떻게 만들어지는지, 어떻게 초기화되어 있는지에 신경 쓰지 않고
		//팩토리로 부터 UserDao 오브젝트를 받아다가 자신의 관심사인 테스트를 위해 활용하기만 하면 됨.
		UserDao dao = new DaoFactory().userDao();
		
		User user = new User();
		user.setId("momo02");
		user.setName("모모");
		user.setPassword("1234");
		
		dao.add(user);
		
		System.out.println(user.getId() + " 등록 성공");
		
		User user2 = dao.get(user.getId());
		
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
		
		System.out.println(user2.getId() + " 조회 성공");
	}
}
