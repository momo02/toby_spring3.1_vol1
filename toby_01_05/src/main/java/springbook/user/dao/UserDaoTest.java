package springbook.user.dao;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import springbook.user.domain.User;

// 관계설정 책임이 추가된 UserDao 클라이언트인 UserDaoTest의 main메소드 
// UserDaoTests는 UserDao와 ConnectionMaker구현 클래스와의 런타임 오브젝트 의존관계를 설정하는 책임을 담당.
public class UserDaoTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		//old
		//UserDao dao = new DaoFactory().userDao();
		
		//new
		//DaoFactory처럼 @Configuration이 붙은 자바 코드를 설정정보로 사용하려면 AnnotationConfigApplicationContext를 이용.
		ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		//getBean메소드는 ApplicationContext가 관리하는 오브젝트를 요청하는 메소드.
		//getBean()은 기본적으로 Object타입으로 리턴하게 되어 있어 매번 리턴되는 오브젝트에 다시 캐스팅을 해줘야함.
		//자바 5 이상의 generic메소드 방식을 사용해 getBean의 두번째 파라미터에 리턴 타입을 주면 캐스팅 코드를 사용하지 않아도 됨.
		UserDao dao = context.getBean("userDao",UserDao.class); 
		
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
