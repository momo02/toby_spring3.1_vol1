package springbook.user.service;

import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

//사용자 관리 비즈니스 로직을 담을 클래스 
public class UserService {
	//테스트와 애플리케이션 코드에 중복되는 상수 값(로그인 횟수, 추천 수)을 정수형 상수로 변경. 
	//--> 업그레이드 조건 값이 바뀌는 경우 UserService의 상수 값만 변경해주면 됨.
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	private UserDao userDao;
	
	private PlatformTransactionManager transactionManager;

	private MailSender mailSender;

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	//new -> 비즈니스 로직과 트랜잭션 경계설정의 분리
	//사용자 레벨 업그레이드 
	public void upgradeLevels() throws Exception {
		//트랜잭션을 가져오는 요청인 getTransaction()메소드 호출 --> 트랜잭션 시작
		//(필요에 따라 트랜잭션 매니저가 DB커넥션을 가져오는 작업도 같이 수행)
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition()); //DefaultTransactionDefinition은 트랜잭션에 대한 속성을 담고 있음.
		/*
		 * 트랜잭션 동기화가 되어 있는 채로 JdbcTemplate를 사용하면, JdbcTemplate의 작업에서 동기화시킨 DB커넥션을 사용.
		 * 이후 UserDao를 통해 진행되는 모든 JDBC작업은 upgradeLevels메소드에서 만든 Connection오브젝트를 사용하고 같은 트랜잭션에 참여하게 됨.
		 */
		try{
			//모든 사용자 정보를 가져와 한 명씩 업그레이드가 가능한지 확인하고, 가능하면 업그레이드를 한다.
// old
//			List<User> users = userDao.getAll();
//			for(User user : users){
//				if(canUpgradeLevel(user)){
//					upgradeLevel(user);
//				}
//			}
			upgradeLevelsInternal();
			this.transactionManager.commit(status); 
		}catch (Exception e) {
			this.transactionManager.rollback(status);
			throw e;
		}
	}
	
	//new -> 분리된 비즈니스 로직 코드. 트랜잭션을 적용하기 전과 동일.
	private void upgradeLevelsInternal() { 
		List<User> users = userDao.getAll();                                                                                                                        
		for(User user : users){                                                                                                                                     
			if(canUpgradeLevel(user)){                                                                                                                              
				upgradeLevel(user);                                                                                                                                 
			}                                                                                                                                                       
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
		//new -> 레벨이 업그레이드되는 사용자에게는 안내 메일을 발송
		sendUpgradeEMail(user);
	}

	//사용자 신규 등록 로직을 담은 add() 메소드 
	public void add(User user) {
		if(user.getLevel() == null) user.setLevel(Level.BASIC);
		userDao.add(user);
	}
	
	/*	<< JavaMail을 이용한 테스트의 문제 >> 

		실제 메일 전송을 수행하는 javaMail 대신에 
		테스트에서 사용할 JavaMail과 같은 인터페이스를 같는 오브젝트를 만들어서 사용하면 
		불필요한 메일 전송 요청을 보내지않는 테스트가 가능하나.. 
		
		But! JavaMail의 API는 이 방법을 적용할수 X. 
		JavaMail의 핵심 API에는 DataSource처럼 인터페이스로 만들어져서 구현을 바꿀 수 있는 게 없음.
		
		==> 이런 경우 '서비스 추상화'를 적용.
		스프링은 JavaMail을 사용해 만든 코드는 쉽게 테스트하기 힘들다는 문제를 해결하기 위해서
		JavaMail의 서비스 추상화 인터페이스인 MailSender를 제공. 
		
		이 인터페이스는 SimpleMailMessage라는 인터페이스를 구현한 클래스에 담긴 메일 메시지를 전송하는 메소드로만 구성.
		기본적으로 JavaMail을 사용해 메일 발송 기능을 제공하는 JavaMailSenderImpl 클래스(MailSender 구현 클래스)를 이용하면 됨.
	 */
	
	//스프링의 MailSender를 이용한 메일 발송 메소드
	private void sendUpgradeEMail(User user) {
// old 
//		JavaMailSenderImpl mailSender = new JavaMailSenderImpl(); //MailSender 구현 클래스의 오브젝트를 생성
//		//JavaMailSenderImpl은 내부적으로 JavaMail API를 이용해 메일을 전송해준다.
//		mailSender.setHost("mail.server.com");
		
		//new -> JavaMailSenderImpl 클래스가 구현한 MailSender 인터페이스만 남기고,구체적인 메일 전송 구현을 담은 클래스의 정보는 코드에서 모두 제거.
		//메일 발송 호스트를 설정하는 코드도 제거.
		
		//MailMessage 인터페이스의 구현 클래스 오브젝트(SimpleMailMessage)를 만들어 메일 내용을 작성.
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("useradmin@ksug.org");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 " + user.getLevel().name() + "로 업그레이드되었습니다");
		
		mailSender.send(mailMessage);
		//cf. 스프링의 예외처리 원칙에 따라 JavaMail을 처리하는 중 발생한 각종 예외를 MailException이라는 런타임 예외로 포장해 던져주기 때문에
		//귀찮은 try/catch 블록을 만들지 않아도 된다.
	}
}
