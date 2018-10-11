package springbook.user.dao;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import springbook.user.domain.User;

// 관계설정 책임이 추가된 UserDao 클라이언트인 UserDaoTest의 main메소드 
// UserDaoTests는 UserDao와 ConnectionMaker구현 클래스와의 런타임 오브젝트 의존관계를 설정하는 책임을 담당.
public class UserDaoTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		//===== 직접 생성한 DaoFactory 오브젝트 출력 코드 ===== 
		DaoFactory factory = new DaoFactory();
		UserDao dao1 = factory.userDao(); //새로운 UserDao 객체를 만들어 리턴
		UserDao dao2 = factory.userDao();
		
		//두 개는 각기 다른 값을 가진 동일하지 않은 오브젝트. 
		System.out.println(dao1);
		System.out.println(dao2);
		
		//===== 스프링 컨택스트로부터 가져온 오브젝트 출력 코드 ===== 
		ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		
		UserDao dao3 = context.getBean("userDao",UserDao.class); 
		UserDao dao4 = context.getBean("userDao",UserDao.class); 
		
		// getBean()을 두 번 호출해서 가져온 오브젝트가 동일.
		// 스프링은 여러 번에 걸쳐 빈을 요청하더라도 매번 동일한 오브젝트를 돌려준다. 매번 new에 의해 새로운 UserDao가 만들어지지 않는다.
		// => 스프링은 기본적으로 별다른 설정을 하지 않으면 내부에서 생성하는 빈 오브젝트를 모두 싱글톤으로 만든다.
		System.out.println(dao3);
		System.out.println(dao4);
		System.out.println(dao3 == dao4);
		
//		User user = new User();
//		user.setId("momo02");
//		user.setName("모모");
//		user.setPassword("1234");
//		
//		dao.add(user);
//		
//		System.out.println(user.getId() + " 등록 성공");
//		
//		User user2 = dao.get(user.getId());
//		
//		System.out.println(user2.getName());
//		System.out.println(user2.getPassword());
//		
//		System.out.println(user2.getId() + " 조회 성공");
	}
}
