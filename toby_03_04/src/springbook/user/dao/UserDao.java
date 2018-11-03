package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

public class UserDao {
	// UserDao에 주입될 의존 오브젝트 타입을 ConnectionMaker에서 DataSource로 변경.
	private DataSource dataSource;
	private JdbcContext jdbcContext;
	
	public void setDataSource(DataSource dataSource){
		//new -> DI 컨테이너가 setDataSource 메소드를 호출하여 DataSource 오브젝트를 주입해줄 때, JdbcContext에 대한 수동 DI 작업을 진행.
		/* 	장점 : 굳이 인터페이스를 두지 않아도 될 만큼 긴밀한 관계를 갖는 DAO클래스와 JdbcContext를 어색하게 따로 빈으로 분리하지 않고
		 	내부에서 직접 만들어 사용하면서도 다른 오브젝트에 대한 DI를 적용할 수 있다. 
		 	단점 : JdbcContext를 여러 오브젝트가 사용하더라고 싱글톤으로 만들 수 없고, DI 작업을 위한 부가적인 코드가 필요하다.
		*/
		this.jdbcContext = new JdbcContext();
		this.jdbcContext.setDataSource(dataSource);
		this.dataSource = dataSource; //아직 JdbcContext를 적용하지 않은 메소드를 위해 저장해둠.
	}
	
//old	
//	private JdbcContext jdbcContext;
//	
//	// JdbcContext를 DI 받도록 만든다.
//	public void setJdbcContext(JdbcContext jdbcContext) {
//		this.jdbcContext = jdbcContext;
//	}

	// *내부 클래스에서 외부의 변수를 사용할 때는 외부 변수는 반드시 final로 선언해줘야 함.
	// (user파라미터는 메소드 내부에서 변경될 일이 없으므로 final로 선언해도 무방)
	public void add(final User user) throws ClassNotFoundException, SQLException{

		// DI 받은 JdbcContext의 컨텍스트 메소드를 사용하도록 변경
		this.jdbcContext.workWithStatementStrategy(new StatementStrategy() { 
			public PreparedStatement makeStatement(Connection c) throws SQLException {
				PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
				ps.setString(1, user.getId());                                                                   
				ps.setString(2, user.getName());                                                                 
				ps.setString(3, user.getPassword());                                                             
				return ps;                                                                                       
			}
		});
	}
	
	// id에 해당하는 데이터가 없으면 EmptyResultDataAccessException 에러를 던지도록 수정한 get메서드
	public User get(String id) throws ClassNotFoundException, SQLException {
		Connection c = dataSource.getConnection(); 
		
		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		
		User user = null; //User는 null 상태로 초기화
		if(rs.next()){ //id를 조건으로 한 쿼리의 결과가 있으면 User오브젝트를 만들고 값을 넣어준다.
			user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
		}
		
		rs.close();
		ps.close();
		c.close();
		
		if(user == null) throw new EmptyResultDataAccessException(1);
		
		return user;
	}
	
	//USERS 테이블의 모든 레코드를 삭제
	public void deleteAll() throws SQLException {
		
		this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {
			public PreparedStatement makeStatement(Connection c) throws SQLException {
				PreparedStatement ps = c.prepareStatement("delete from users");
				return ps;
			}
		});
	}
	
	//USERS 테이블의 레코드 개수를 돌려줌
	public int getCount() throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			c = dataSource.getConnection();
			ps = c.prepareStatement("select count(*) from users");
			//ResultSet도 다양한 SQLException이 발생할 수 있는 코드이므로 try블록 안에 둬야 함.
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);
			
		}catch(Exception e){
			throw e;
		}finally{
			//cf) close()는 만들어진 순서의 반대로 하는 것이 원칙.
			if(rs != null){
				try{
					rs.close();
				}catch(SQLException e){
					
				}
			}
			if(ps != null){
				try{
					ps.close();
				}catch(SQLException e){
					
				}
			}
			if(c != null){
				try{
					c.close();
				}catch(SQLException e){
					
				}
			}
		}
	}
	
}
