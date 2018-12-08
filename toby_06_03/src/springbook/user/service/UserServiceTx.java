package springbook.user.service;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.domain.User;

public class UserServiceTx implements UserService {
	private UserService userService;
	private PlatformTransactionManager transactionManager;
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public void add(User user) {
		this.userService.add(user);
	}

	@Override
	public void upgradeLevels() {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition()); //DefaultTransactionDefinition은 트랜잭션에 대한 속성을 담고 있음.    
		/*                                                                                                                                                            
		 * 트랜잭션 동기화가 되어 있는 채로 JdbcTemplate를 사용하면, JdbcTemplate의 작업에서 동기화시킨 DB커넥션을 사용.                                                                                 
		 * 이후 UserDao를 통해 진행되는 모든 JDBC작업은 upgradeLevels메소드에서 만든 Connection오브젝트를 사용하고 같은 트랜잭션에 참여하게 됨.                                                                 
		 */                                                                                                                                                           
		try{                                                                                                                                                          
			
			userService.upgradeLevels();
			
			this.transactionManager.commit(status);                                                                                                                   
		}catch (Exception e) {                                                                                                                                        
			this.transactionManager.rollback(status);                                                                                                                 
			throw e;                                                                                                                                                  
		}                                                                                                                                                             
	}

}
