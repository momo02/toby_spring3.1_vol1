package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//독립시킨 DB 연결 기능인 SimpleConnectionMaker
public class SimpleConnectionMaker { // 더 이상 상속을 이용한 확장 방식을 사용할 필요가 없으니 추상 클래스도 만들 필요 없음.
	public Connection makeNewConnection() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/toby_spring?characterEncoding=UTF-8","root","258080");
		return c;
	}
}
