package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import springbook.user.domain.User;

// JDBC를 이용한 등록과 조회 기능이 있는 UserDao 클래스
public class UserDao {
	private ConnectionMaker connectionMaker; //인터페이스를 통해 오브젝트에 접근하므로 구체적인 클래스 정보를 알 필요가 없다. 
	
	public UserDao(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker; 
	}
	
	// 새로운 사용자를 add.
	public void add(User user) throws ClassNotFoundException, SQLException{
		
		//인터페이스에 정의된 메소드를 사용하므로
		//클래스가 바뀐다고 해도 메소드 이름이 변경될 걱정은 없다. 
		Connection c = connectionMaker.makeConnection(); 
		
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
