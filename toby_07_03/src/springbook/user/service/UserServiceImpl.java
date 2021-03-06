package springbook.user.service;

import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

//사용자 관리 비즈니스 로직을 담을 클래스 
public class UserServiceImpl implements UserService {
	//테스트와 애플리케이션 코드에 중복되는 상수 값(로그인 횟수, 추천 수)을 정수형 상수로 변경. 
	//--> 업그레이드 조건 값이 바뀌는 경우 UserService의 상수 값만 변경해주면 됨.
	public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	private UserDao userDao;

	private MailSender mailSender;

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	//사용자 레벨 업그레이드 
	public void upgradeLevels() {
			//모든 사용자 정보를 가져와 한 명씩 업그레이드가 가능한지 확인하고, 가능하면 업그레이드를 한다.
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
	
	//스프링의 MailSender를 이용한 메일 발송 메소드
	private void sendUpgradeEMail(User user) {
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

	//new :: 추가 메소드 구현
	//=> 이제 모든 User 관련 데이터 조작은 UserService라는 트랜잭션 경계를 통해 진행할 경우 모두 트랜잭션을 적용할 수 있게 됐다.
	public void deleteAll() {
		userDao.deleteAll();
	}
	
	public User get(String id) {
		return userDao.get(id);
	}

	public List<User> getAll() {
		return userDao.getAll();
	}

	public void update(User user) {
		userDao.update(user);
	}
	
}
