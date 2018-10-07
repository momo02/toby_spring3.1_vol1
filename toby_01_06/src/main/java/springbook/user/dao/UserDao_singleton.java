package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import springbook.user.domain.User;

// 자바에서 싱글톤 패턴을 구현하는 방법에 따라 
// 싱글톤 패턴을 적용한 UserDao
public class UserDao_singleton {
	//생성된 싱글톤 오브젝트를 저장할 수 있는 자신과 같은 타입의 static필드를 정의.
	private static UserDao INSTANCE;
	
	private ConnectionMaker connectionMaker; 
	
	//클래스 밖에서는 오브젝트를 생성하지 못하도록 생성자를 private로 만듬.
	private UserDao_singleton(ConnectionMaker connectionMaker) {
		this.connectionMaker = connectionMaker; 
	}
	
	//static 팩토리 메소드인 getInstance()를 만들고 이 메소드가 최초로 호출되는 시점에서 한 번만 오브젝트가 만들어지게 함. 
	//생성된 오브젝트는 static필드에 저장됨. 또는 static필드의 초기값으로 오브젝트를 미리 만들어 둘 수도 있음. 
	//한 번 오브젝트(싱글톤)가 만들어지고 난 후엔 getInstance()메소드를 통해 이미 만들어진 static필드에 저장해둔 오브젝트를 넘겨줌. 
	public static synchronized UserDao getInstance(ConnectionMaker connectionMaker){
		if(INSTANCE == null) INSTANCE = new UserDao(connectionMaker);
		return INSTANCE;
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
