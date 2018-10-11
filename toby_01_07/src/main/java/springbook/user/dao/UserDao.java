package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import springbook.user.domain.User;

public class UserDao {
	/* 
	 * 의존관계 '검색'은 런타임 시 의존관계를 맺을 오브젝트를 결정하는 것과 오브젝트의 생성 작업은 외부 컨테이너에게 IoC로 맡기지만,
	 * 이를 가져올 때는 메소드나 생성자를 통한 주입 대신 스스로 컨테이너에게 요청하는 방법을 사용.
	 */
	private ConnectionMaker connectionMaker; 

	//old (의존관계 주입을 위한 코드)
//	public UserDao(ConnectionMaker connectionMaker) {
//		this.connectionMaker = connectionMaker; 
//	}
	
	//new 
	//의존관계 검색을 이용하는 UserDao 생성자
	public UserDao() {
		//DaoFactory를 이용하는 생성자
		//(ConnectionMaker 타입 오브젝트를 외부로부터의 주입이 아니라 스스로 IoC컨테이너인 DaoFactory에게 요청)
//		DaoFactory daoFactory = new DaoFactory();
//		this.connectionMaker = daoFactory.connectionMaker();
		
		//=> 이런 작업을 일반화한 스프링의 애플리케이션 컨텍스트라면 미리 정해놓은 이름을 전달해서 그 이름에 해당하는 오브젝트를 찾는다.(검색)
		//또한 그 대상이 런타임 의존관계를 가질 오브젝트이므로 '의존관계 검색'이라 부름.
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		this.connectionMaker = context.getBean("connectionMaker",ConnectionMaker.class);
	}
	
	public void add(User user) throws ClassNotFoundException, SQLException{
		Connection c = connectionMaker.makeConnection(); 
		
		PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
	}
	
	public User get(String id) throws ClassNotFoundException, SQLException {
		Connection c = connectionMaker.makeConnection(); 
		
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		
		User user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		
		rs.close();
		ps.close();
		c.close();
		
		return user;
	}

}
