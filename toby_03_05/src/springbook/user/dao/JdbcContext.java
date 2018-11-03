package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

// JDBC 작업 흐름을 분리해서 만든 JdbcContext 클래스
public class JdbcContext {
	private DataSource dataSource;
	
	//DataSource타입 빈을 DI 받을 수 있게 준비해둔다.
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void workWithStatementStrategy(StatementStrategy stmt) throws SQLException{     
        //클라이언트가 컨텍스트를 호출할 때 넘겨줄 전략 파라미터                 
		Connection c = null;                                                                      
		PreparedStatement ps = null;                                                              
		
		try{                                                                                      
			c = dataSource.getConnection();                                                       
			ps = stmt.makeStatement(c);                                                           
			ps.executeUpdate();                                                                   
		}catch(SQLException e){                                                                   
			throw e;                                                                              
		}finally{                                                                                 
			if(ps != null){ try{ ps.close(); }catch(SQLException e){ } }                          
			if(c != null){ try{ c.close(); }catch(SQLException e){ } }                            
		}                                                                                         
	} 
	
	//new
	/*	이렇게 재사용 가능한 콜백을 담고 있는 메소드라면 DAO가 공유할 수 있는 템플릿 클래스 안으로 옮겨도 됨.
		엄밀히 말해 템플릿은 JdbcContext클래스가 아니라 workWithStatementStrategy()메소드 이므로 
		JdbcContext클래스로 콜백 생성과 템플릿 호출이 담긴 executeSql()메소드를 옮긴다고 해도 문제 될 것은 없음 */
	public void executeSql(final String query) throws SQLException{                    
		workWithStatementStrategy(new StatementStrategy() {            
			public PreparedStatement makeStatement(Connection c) throws SQLException {  
				PreparedStatement ps = c.prepareStatement(query);                       
				return ps;                                                              
			}                                                                           
		});                                                                             
	}                                                                                   
}
