package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// deleteAll()메소드의 기능을 위해 만든 전략 클래스
public class DeleteAllStatement implements StatementStrategy{

	public PreparedStatement makeStatement(Connection c) throws SQLException {
		PreparedStatement ps = c.prepareStatement("delete from users");
		return ps;
	}

}
