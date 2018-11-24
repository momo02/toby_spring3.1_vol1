package springbook.user.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.Level;
import springbook.user.domain.User;
/*
 * 4.2.4 기술에 독립적인 UserDao 만들기
 * - 인터페이스 적용
 *  */
public class UserDaoJdbc implements UserDao {
	private JdbcTemplate jdbcTemplate;
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
			return user;
		}
	};
	
	public void setDataSource(DataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);  //생성자의 파라미터로 DataSource를 주입.
	}
	
	public void add(final User user) {
		this.jdbcTemplate.update("insert into users(id, name, password, level, login, recommend) values(?,?,?,?,?,?)",
									user.getId(),user.getName(),user.getPassword(),user.getLevel().intValue(),user.getLogin(),user.getRecommend());
		
	}
	
	public User get(String id) {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?"
										  ,new Object[] {id} //SQL에 바인딩할 파라미터 값, 가변인자 대신 Object타입 배열을 사용.(뒤에 다른 파라미터가 있기 때문..) 
										                     //배열 초기화 블럭을 사용해 SQL의 ?에 바인딩할 id 값 전달. 
										  ,userMapper);
	}
	
	//USERS 테이블의 모든 레코드를 삭제
	public void deleteAll() {
		this.jdbcTemplate.update("delete from users");
	}
	
	//USERS 테이블의 레코드 개수를 돌려줌
	public int getCount() {
		return this.jdbcTemplate.queryForInt("select count(*) from users");
	}
	
	//USERS 테이블의 모든 레코드를 가져옴(기본키인 id순으로 정렬) 
	public List<User> getAll() { 
		//cf. 바인딩할 파라미터가 있다면 두번째 파라미터에 추가할 수도 있음. 
		return this.jdbcTemplate.query("select * from users order by id", userMapper);
	}

	public void update(User user) {
		this.jdbcTemplate.update(
				"update users set name = ?, password = ?, level =?, login =?, recommend = ? where id = ? "
				,user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId());
		
	}
	
}