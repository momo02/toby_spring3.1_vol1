package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserDaoDeleteAll extends UserDao{
	//makeStatement()를 구현한 UserDao 서브클래스
	protected PreparedStatement makeStatement(Connection c) throws SQLException {
		PreparedStatement ps = c.prepareStatement("delete from users");
		return ps;
	}
	
	/*
	 * 상속을 통해 확장을 꾀하는 템플릿 메소드 패턴의 단점
	 * - DAO로직마다 상속을 통해 새로운 클래스를 만들어야함.
	 * - 확장구조가 이미 클래스를 설계하는 시점에서 고정되어 버림 -> 관계에 대한 유연성이 떨어짐.
	 */
}
