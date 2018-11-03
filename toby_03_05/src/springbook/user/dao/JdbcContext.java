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
	
}
