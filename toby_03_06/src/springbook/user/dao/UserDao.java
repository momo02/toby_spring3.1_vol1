package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.User;

public class UserDao {
	// UserDao에 주입될 의존 오브젝트 타입을 ConnectionMaker에서 DataSource로 변경.
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	public void setDataSource(DataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);  //생성자의 파라미터로 DataSource를 주입.
		this.dataSource = dataSource; //아직 JdbcContext를 적용하지 않은 메소드를 위해 저장해둠.
	}
	
	// *내부 클래스에서 외부의 변수를 사용할 때는 외부 변수는 반드시 final로 선언해줘야 함.
	// (user파라미터는 메소드 내부에서 변경될 일이 없으므로 final로 선언해도 무방)
	public void add(final User user) throws ClassNotFoundException, SQLException{
		this.jdbcTemplate.update("insert into users(id, name, password) values(?,?,?)",
									user.getId(),user.getName(),user.getPassword());
		
	}
	
	// id에 해당하는 데이터가 없으면 EmptyResultDataAccessException 에러를 던지도록 수정한 get메서드
	public User get(String id) throws ClassNotFoundException, SQLException {
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
		// JdbcTemplate의 콜백과 템플릿 메소드를 사용
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
		// JdbcTemplate 적용
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
	
	//query() 템플릿을 이용하는 getAll() 구현
	/* - queryForObject()는 쿼리의 결과가 로우 하나일 때 사용하고, query()는 여러 개의 로우가 결과로 나오는 경우에 사용. 
	 * - query()의 리턴 타임의 List<T>. query()은 제네릭 메소드로 타입은 파라미터로 넘기는 RowMapper<T>콜백 오브젝트에서 결정됨.
	 * - query() 템플릿은 SQL을 실행해서 얻은 ResultSet의 모든 로우를 열람하면서 로우마다 RowMapper콜백을 호출. 
	 *   이렇게 만들어진 User오브젝트들은 템플릿이 미리 준비한 List<User> 컬렉션에 추가됨
	 * */
	public List<User> getAll() { 
		//cf. 바인딩할 파라미터가 있다면 두번째 파라미터에 추가할 수도 있음. 
		return this.jdbcTemplate.query("select * from users order by id"  
										,new RowMapper<User>(){
											public User mapRow(ResultSet rs, int rowNum) throws SQLException {
												User user = new User();
												user.setId(rs.getString("id"));
												user.setName(rs.getString("name"));
												user.setPassword(rs.getString("password"));
												return user;
											}
										});
	}
	
}