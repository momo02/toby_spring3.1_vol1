package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.sqlservice.SqlService;
/*
 * 4.2.4 기술에 독립적인 UserDao 만들기
 * - 인터페이스 적용
 *  */
public class UserDaoJdbc implements UserDao {
	private JdbcTemplate jdbcTemplate;
	
	//old :: XML설정을 이용한 SQL분리 - Map타입의 sqlMap프로퍼티 추가
	//private Map<String, String> sqlMap;
	
	//new :: SqlService 프로퍼티 추가 
	private SqlService sqlService; 
	
	// 재사용 가능하도록 독립시킨 RowMapper
	private RowMapper<User> userMapper = new RowMapper<User>(){ //ResultSet 한 로우의 결과를 오브젝트에 매핑해주는 RowMapper 콜백
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));              
			user.setName(rs.getString("name"));          
			user.setPassword(rs.getString("password"));  
			user.setLevel(Level.valueOf(rs.getInt("level")));
			user.setLogin(rs.getInt("login"));
			user.setRecommend(rs.getInt("recommend"));
			user.setEmail(rs.getString("email"));
			return user;
		}
	};

//old	
//	public Map<String, String> getSqlMap() {
//		return sqlMap;
//	}
//
//	public void setSqlMap(Map<String, String> sqlMap) {
//		this.sqlMap = sqlMap;
//	}
	
	public SqlService getSqlService() {
		return sqlService;
	}

	public void setSqlService(SqlService sqlService) {
		this.sqlService = sqlService;
	}

	public void setDataSource(DataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);  //생성자의 파라미터로 DataSource를 주입.
	}

	public void add(final User user) {
		//old 
		//this.jdbcTemplate.update(this.sqlMap.get("add"),
		//		user.getId(),user.getName(),user.getPassword(),user.getLevel().intValue(),user.getLogin(),user.getRecommend(),user.getEmail());
		
		//new
		this.jdbcTemplate.update(this.sqlService.getSql("userAdd"),
				user.getId(),user.getName(),user.getPassword(),user.getLevel().intValue(),user.getLogin(),user.getRecommend(),user.getEmail());
	}
	
	public User get(String id) {
		//old
		//return this.jdbcTemplate.queryForObject(this.sqlMap.get("get")
		//		  ,new Object[] {id} //SQL에 바인딩할 파라미터 값, 가변인자 대신 Object타입 배열을 사용.(뒤에 다른 파라미터가 있기 때문..) 
		//		                     //배열 초기화 블럭을 사용해 SQL의 ?에 바인딩할 id 값 전달. 
		//		  ,userMapper);
		
		//new
		return this.jdbcTemplate.queryForObject(this.sqlService.getSql("userGet")
				  ,new Object[] {id} //SQL에 바인딩할 파라미터 값, 가변인자 대신 Object타입 배열을 사용.(뒤에 다른 파라미터가 있기 때문..) 
				                     //배열 초기화 블럭을 사용해 SQL의 ?에 바인딩할 id 값 전달. 
				  ,userMapper);
	
	}
	
	//USERS 테이블의 모든 레코드를 삭제
	public void deleteAll() {
		//old
		//this.jdbcTemplate.update(this.sqlMap.get("deleteAll"));
		
		//new
		this.jdbcTemplate.update(this.sqlService.getSql("userDeleteAll"));
	}
	
	//USERS 테이블의 레코드 개수를 돌려줌
	public int getCount() {
		//old
		//return this.jdbcTemplate.queryForInt(this.sqlMap.get("getCount"));
		
		//new
		return this.jdbcTemplate.queryForInt(this.sqlService.getSql("userGetCount"));
	}
	
	//USERS 테이블의 모든 레코드를 가져옴(기본키인 id순으로 정렬) 
	public List<User> getAll() { 
		//cf. 바인딩할 파라미터가 있다면 두번째 파라미터에 추가할 수도 있음. 
		//old 
		//return this.jdbcTemplate.query(this.sqlMap.get("getAll"), userMapper);
		
		//new
		return this.jdbcTemplate.query(this.sqlService.getSql("userGetAll"), userMapper);
	}

	public void update(User user) {
		//old
		//this.jdbcTemplate.update(
		//		this.sqlMap.get("update")
		//		,user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail(), user.getId());
	
		//new
		this.jdbcTemplate.update(
				this.sqlService.getSql("userUpdate")
				,user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail(), user.getId());
	
	}
	
}