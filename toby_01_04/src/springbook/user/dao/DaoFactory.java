package springbook.user.dao;

import java.sql.Connection;

//UserDao의 생성 책임을 맡은 팩토리 클래스
public class DaoFactory {
	//아래와 같이 AccountDao,MessageDao 등을 새로 만든다면..
	//어떤 ConnectionMaker 구현 클래스를 사용할지를 결정하는 기능이 중복돼서 나타남.. 
//	public UserDao userDao(){
//		return new UserDao(new DConnectionMaker());
//	}
//	
//	public AccountDao accountDao(){
//		return new UserDao(new DConnectionMaker());
//	}
//	
//	public MessageDao messageDao(){
//		return new UserDao(new DConnectionMaker());
//	}
	
	public UserDao userDao(){
		return new UserDao(connectionMaker());
	}
	
//	public AccountDao accountDao(){                 
//		return new UserDao(connectionMaker()); 
//	}                                               
//	                                                
//	public MessageDao messageDao(){                 
//		return new UserDao(connectionMaker()); 
//	}          
	
	//ConnectionMaker의 구현 클래스를 결정하고 오브젝트를 만드는 코드를 별도의 메소드로 뽑아냄. 
	//ConnectionMaker의 구현 클래스를 바꿀 필요가 있을 때도 딱 한 군데만 수정하면 모든 DAO 팩토리 메소드에 적용됨
	public ConnectionMaker connectionMaker(){
		return new DConnectionMaker(); //분리해서 중복을 제거한 ConnectionMaker타입 오브젝트 생성 코드
	}
	
}
