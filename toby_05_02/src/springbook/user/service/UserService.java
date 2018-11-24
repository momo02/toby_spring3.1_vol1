package springbook.user.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

//사용자 관리 비즈니스 로직을 담을 클래스 
public class UserService {
	//테스트와 애플리케이션 코드에 중복되는 상수 값(로그인 횟수, 추천 수)을 정수형 상수로 변경. 
	//--> 업그레이드 조건 값이 바뀌는 경우 UserService의 상수 값만 변경해주면 됨.
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	UserDao userDao;
	
	//UserDao오브젝트의 DI가 가능하도록 수정자 메소드 추가.
	public UserDao getUserDao() {
		return userDao;
	}
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	//new 트랜잭션 동기화 방식을 적용한 UserService 
	// Connection을 생성할 때 사용할 DataSource를 DI 받도록 한다.
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}
	
	//사용자 레벨 업그레이드 
	public void upgradeLevels() throws Exception {
		//트랜잭션 동기화 관리자를 이용해 동기화 작업을 초기화
		TransactionSynchronizationManager.initSynchronization();
		//DB커넥션을 생성하고 트랜잭션을 시작. 이후의 DAO작업은 모두 여기서 시작한 트랜잭션 안에서 진행된다.
		Connection c = DataSourceUtils.getConnection(dataSource);
		// cf. datasource.getConnection() -> 이렇게 DataSource에서 Connection을 직접 가져오지 않고, 
		// 스프링 유틸리티 메소드인 DataSourceUtils.getConnection()을 쓰는 이유는 
		// Connection 오브젝트를 생성해줄 뿐만 아니라 트랜잭션 동기화에 사용하도록 저장소에 바인딩해주기 때문.
		c.setAutoCommit(false);
		
		/*
		 * 트랜잭션 동기화가 되어 있는 채로 JdbcTemplate를 사용하면, JdbcTemplate의 작업에서 동기화시킨 DB커넥션을 사용.
		 * 이후 UserDao를 통해 진행되는 모든 JDBC작업은 upgradeLevels메소드에서 만든 Connection오브젝트를 사용하고 같은 트랜잭션에 참여하게 됨.
		 */
		try{
			//모든 사용자 정보를 가져와 한 명씩 업그레이드가 가능한지 확인하고, 가능하면 업그레이드를 한다.
			List<User> users = userDao.getAll();
			for(User user : users){
				if(canUpgradeLevel(user)){
					upgradeLevel(user);
				}
			}
			c.commit(); 
		}catch (Exception e) {
			c.rollback();
			throw e;
		}finally{
			//스프링 유틸리티 메소드를 이용해 DB커넥션을 안전하게 닫는다. 
			DataSourceUtils.releaseConnection(c, dataSource);
			//동기화 작업 종료 및 정리
			TransactionSynchronizationManager.unbindResource(this.dataSource);
			TransactionSynchronizationManager.clearSynchronization();
		}
	}
	
	//업그레이드 가능 여부 확인 메소드 
	private boolean canUpgradeLevel(User user){
		Level currentLevel = user.getLevel();
		//레벨별로 구분해서 조건을 판단한다.
		switch(currentLevel) {
			case BASIC : return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER : return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
			case GOLD : return false;
			//현재 로직에서 다룰 수 없는 레벨이 주어지면 예외를 발생시킴. 새로운 레벨이 추가되고 로직을 수정하지 않으면 에러가 나서 확인할 수 있다. 
			default : throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		}
	}
	
	//레벨 업그레이드 작업 메소드
	// 사용자 오브젝트의 레벨정보를 다음 단계로 변경하고, 변경된 오브젝트를 DB에 업데이트하는 두 가지 작업을 수행.
	protected void upgradeLevel(User user){
		user.upgradeLevel();
		userDao.update(user);
	}
	
	//사용자 신규 등록 로직을 담은 add() 메소드 
	public void add(User user) {
		if(user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}
}
