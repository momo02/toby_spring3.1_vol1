package springbook.user.dao;

//UserDao의 생성 책임을 맡은 팩토리 클래스
public class DaoFactory {
	public UserDao userDao(){
		//팩토리의 메소드는 UserDao타입의 오브젝트를 어떻게 만들고, 어떻게 준비시킬지를 결정.
		ConnectionMaker connectionMaker = new DConnectionMaker(); 
		UserDao dao = new UserDao(connectionMaker);
		return dao;
	}
}
