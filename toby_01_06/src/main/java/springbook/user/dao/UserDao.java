package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import springbook.user.domain.User;

// 인스턴스 변수를 사용하도록 수정한 UserDao
// 기존에 로컬 변수로 선언하고 사용했던 Connection과 User를 클래스의 인스턴스 필드로 선언.
public class UserDao {
	//초기에 설정하면 사용 중에는 바뀌지 않는 읽기전용 인스턴스 변수
	private ConnectionMaker connectionMaker; 
	//new 
	//매번 새로운 값으로 바뀌는 정보를 담은 인스턴스 변수. 
	//(저장할 공간이 하나 뿐이여서 서로 값을 덮어쓰고 자신이 저장하지 않은 값을 읽어올 수 있음
	//즉 싱글톤으로 만들어져 멀티스레드 환경에서 사용하면..여러 사용자 동시 접속 시 데이터가 엉망이 돼버리는 등의 심각한 문제 발생!)
	private Connection c; 
	private User user;
	
	public UserDao(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker; 
	}
	
	// 새로운 사용자를 add.
	public void add(User user) throws ClassNotFoundException, SQLException{
		//old
		//Connection c = connectionMaker.makeConnection(); 
		//new 
		this.c = connectionMaker.makeConnection(); 
		
		PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
	}
	
	//아이디를 가지고 사용자 정보를 get.
	public User get(String id) throws ClassNotFoundException, SQLException {
		//old
		//Connection c = connectionMaker.makeConnection(); 
		//new 
		this.c = connectionMaker.makeConnection();
		
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		//old
		//User user = new User();
		//new
		this.user = new User();
		user.setId(rs.getString("id"));
		user.setName(rs.getString("name"));
		user.setPassword(rs.getString("password"));
		
		rs.close();
		ps.close();
		c.close();
		
		return user;
	}
	
	/*===================================================================================================================
	 따라서 스프링의 싱글톤 빈으로 사용되는 클래스를 만들 때는 
	 기존의 UserDao처럼 개별적으로 바뀌는 정보는 로컬 변수로 정의하거나, 파라미터로 주고 받으면서 사용하게 해야 함. 
	 (메소드 파라미터나, 메소드 안에서 생성되는 로컬 변수는 매번 새로운 값을 저장할 독립적인 공간이 만들어지기 때문에 
	  싱글톤이라고 해도 여러 스레드가 변수의 값을 덮어쓸 일은 없다.) 
	 또한 읽기전용의 속성을 가진 정보라면 싱글톤에서 인스턴스 변수로 사용해도 좋다.
	  물론 단순한 읽기전용 값이라면 static final이나 final로 선언하는 편이 낫다.
	===================================================================================================================*/

}
