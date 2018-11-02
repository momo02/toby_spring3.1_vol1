package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import springbook.user.domain.User;

// add() 메소드의 PreparedStatement 생성 로직을 분리한 클래스
public class AddStatement implements StatementStrategy{
	User user;
	
	//User정보를 생성자로부터 제공받도록 함.
	public AddStatement(User user){
		this.user = user;
	}
	
	public PreparedStatement makeStatement(Connection c) throws SQLException {
		PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		return ps;
	}
}
