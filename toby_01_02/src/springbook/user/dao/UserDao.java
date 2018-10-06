package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import springbook.user.domain.User;

// JDBC를 이용한 등록과 조회 기능이 있는 UserDao 클래스
public class UserDao {
	// 새로운 사용자를 add.
	public void add(User user) throws ClassNotFoundException, SQLException{
		//old
		//Class.forName("com.mysql.jdbc.Driver");
		//Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/toby_spring?characterEncoding=UTF-8","root","258080");
		
		//new
		Connection c = getConnection();
		
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
		//Class.forName("com.mysql.jdbc.Driver");
		//Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/toby_spring?characterEncoding=UTF-8","root","258080");
		
		//new
		Connection c = getConnection();
		
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
	
	//new 중복된 DB연결 코드를 메소드로 추출.
	private Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/toby_spring?characterEncoding=UTF-8","root","258080");
		return c;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		UserDao dao = new UserDao();
		
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
