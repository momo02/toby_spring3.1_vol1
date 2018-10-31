package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

public class UserDao {
	// UserDao에 주입될 의존 오브젝트 타입을 ConnectionMaker에서 DataSource로 변경.
	private DataSource dataSource;

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	public void add(User user) throws ClassNotFoundException, SQLException{
		Connection c = dataSource.getConnection(); 
		
		PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values(?,?,?)");
		ps.setString(1, user.getId());
		ps.setString(2, user.getName());
		ps.setString(3, user.getPassword());
		
		ps.executeUpdate();
		
		ps.close();
		c.close();
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
		Connection c = null;
		PreparedStatement ps = null;
		try{
			c = dataSource.getConnection();
			ps = c.prepareStatement("delete from users");
			ps.executeUpdate();
		}catch(SQLException e){
			throw e;
		}finally{ //try블록에서 예외가 발생했을 때나 안 했을 때나 모두 실행.
			/* null상태의 변수에 close()메소드를 호출하면 NullPointerException이 발생..
			   어느시점에서 예외가 발생했는지에 따라서 close()를 사용할 수 있는 변수가 달라질 수 있기 떄문에 
			   finally에서는 반드시 c와 ps가 null이 아닌지 먼저 확인한 후에 close()메소드를 호출해야 함. */
			if(ps != null){ 
				try{
					ps.close();
				}catch(SQLException e){
					// ps.close() 메소드에서도 SQLException이 발생할 수 있기 때문에 이를 잡아줘야 함.
					// 그렇지 않으면 Connection을 close()하지 못하고(아래 c.close() 부분이 실행되지 않고) 메소드를 빠져나갈 수 있다. 
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
