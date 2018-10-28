package springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionMaker {
	//DB 커넥션을 생성 
	public Connection makeConnection() throws ClassNotFoundException, SQLException;
}
