package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.User;

public class UserDao {
	// UserDao에 주입될 의존 오브젝트 타입을 ConnectionMaker에서 DataSource로 변경.
	private DataSource dataSource;
	//old
	//private JdbcContext jdbcContext;
	
	//new
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource){
		//old
		// DI 컨테이너가 setDataSource 메소드를 호출하여 DataSource 오브젝트를 주입해줄 때, JdbcContext에 대한 수동 DI 작업을 진행.
		//this.jdbcContext = new JdbcContext();
		//this.jdbcContext.setDataSource(dataSource);
		
		//new 기존 JdbcContext -> 스프링의 JdbcTemplate으로 변경 
		this.jdbcTemplate = new JdbcTemplate(dataSource);  //생성자의 파라미터로 DataSource를 주입.
		this.dataSource = dataSource; //아직 JdbcContext를 적용하지 않은 메소드를 위해 저장해둠.
	}
	
	// *내부 클래스에서 외부의 변수를 사용할 때는 외부 변수는 반드시 final로 선언해줘야 함.
	// (user파라미터는 메소드 내부에서 변경될 일이 없으므로 final로 선언해도 무방)
	public void add(final User user) throws ClassNotFoundException, SQLException{
// old
//		// DI 받은 JdbcContext의 컨텍스트 메소드를 사용하도록 변경
//		this.jdbcContext.workWithStatementStrategy(new StatementStrategy() { 
//			public PreparedStatement makeStatement(Connection c) throws SQLException {
//				PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
//				ps.setString(1, user.getId());                                                                   
//				ps.setString(2, user.getName());                                                                 
//				ps.setString(3, user.getPassword());                                                             
//				return ps;                                                                                       
//			}
//		});
		
	//new 
	this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)",
									user.getId(),user.getName(),user.getPassword());
		
	}
	
	// id에 해당하는 데이터가 없으면 EmptyResultDataAccessException 에러를 던지도록 수정한 get메서드
	public User get(String id) throws ClassNotFoundException, SQLException {
//old
//		Connection c = dataSource.getConnection(); 
//		
//		PreparedStatement ps = c.prepareStatement("select * from users where id = ?");
//		ps.setString(1, id);
//		
//		ResultSet rs = ps.executeQuery();
//		
//		User user = null; //User는 null 상태로 초기화
//		if(rs.next()){ //id를 조건으로 한 쿼리의 결과가 있으면 User오브젝트를 만들고 값을 넣어준다.
//			user = new User();
//			user.setId(rs.getString("id"));
//			user.setName(rs.getString("name"));
//			user.setPassword(rs.getString("password"));
//		}
//		
//		rs.close();
//		ps.close();
//		c.close();
//		
//		if(user == null) throw new EmptyResultDataAccessException(1);
//		
//		return user;
		return this.jdbcTemplate.queryForObject("select * from users where id = ?"
										  ,new Object[] {id} //SQL에 바인딩할 파라미터 값, 가변인자 대신 Object타입 배열을 사용.(뒤에 다른 파라미터가 있기 때문..) 
										                     //배열 초기화 블럭을 사용해 SQL의 ?에 바인딩할 id 값 전달. 
										  ,new RowMapper<User>(){ //ResultSet 한 로우의 결과를 오브젝트에 매핑해주는 RowMapper 콜백
											public User mapRow(ResultSet rs, int rowNum) throws SQLException {
												User user = new User();
												user.setId(rs.getString("id"));              
												user.setName(rs.getString("name"));          
												user.setPassword(rs.getString("password"));  
												return user;
											}
										  });
		/* 
		 * queryForObject()는 SQL을 실행하면 한 개의 로우만 얻을 것이라고 기대.
		 * 그리고 ResultSet의 next()를 실행해 첫 번째 로우로 이동시킨 후에 RowMapper콜백을 호출.
		 * SQL을 실행해서 받은 로우의 개수가 하나가 아니라면 EmptyResultDataAccessException 예외를 던진다.
		 */
		
	}
	
	//USERS 테이블의 모든 레코드를 삭제
	public void deleteAll() throws SQLException {
		//old
		//this.jdbcContext.executeSql("delete from users");
		
		//new -> JdbcTemplate의 콜백과 템플릿 메소드를 사용하도록 수정.
//		this.jdbcTemplate.update(new PreparedStatementCreator(){
//			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				return con.prepareStatement("delete from users");
//			}
//		});
		
		//내장 콜백을 사용하는 update()사용. (기존에 만들었던 executeSql()과 기능이 비슷한 메소드) 
		this.jdbcTemplate.update("delete from users");
	}
	
	//USERS 테이블의 레코드 개수를 돌려줌
	public int getCount() throws SQLException {
//old
//		Connection c = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//		
//		try{
//			c = dataSource.getConnection();
//			ps = c.prepareStatement("select count(*) from users");
//			//ResultSet도 다양한 SQLException이 발생할 수 있는 코드이므로 try블록 안에 둬야 함.
//			rs = ps.executeQuery();
//			rs.next();
//			return rs.getInt(1);
//			
//		}catch(Exception e){
//			throw e;
//		}finally{
//			//cf) close()는 만들어진 순서의 반대로 하는 것이 원칙.
//			if(rs != null){
//				try{
//					rs.close();
//				}catch(SQLException e){
//					
//				}
//			}
//			if(ps != null){
//				try{
//					ps.close();
//				}catch(SQLException e){
//					
//				}
//			}
//			if(c != null){
//				try{
//					c.close();
//				}catch(SQLException e){
//					
//				}
//			}
//		}
		
		//new -> JdbcTemplate 적용
		//  PreparedStatementCreator 콜백은 템플릿으로 부터 Connection을 받고 PreparedStatement를 돌려준다.
		//  ResultSetExtractor 콜백은 템플릿으로 부터 ResultSet을 받고 거기서 추출한 결과를 돌려준다. 
		
//		return this.jdbcTemplate.query(new PreparedStatementCreator() { //1번째 콜백. Statement 생성
//			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				return con.prepareStatement("select count(*) from users");
//			}
//		}, new ResultSetExtractor<Integer>() { //2번째 콜백. ResultSet으로부터 값 추출.
//			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
//				rs.next();
//				return rs.getInt(1);
//			}
//		});
		
		//위 기능(sql의 실행결과로 하나의 정수 값을 리턴)을 가진 콜백을 내장하고 있는 queryForInt() 메소드 사용. 
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}
}