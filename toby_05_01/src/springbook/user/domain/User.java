package springbook.user.domain;

public class User {
	String id;
	String name;
	String password;
	//new 
	Level level; //사용자 레벨 
	int login; //로그인 횟수
	int recommend; //추천수
	
	//new 추가된 필드를 파라미터로 포함하는 생성자
	public User(String id, String name, String password, Level level, int login, int recommend) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.level = level;
		this.login = login;
		this.recommend = recommend;
	}
	
	//자바빈의 규약을 따르는 클래스에 생성자를 명시적으로 추가했을 때는 
	//파라미터가 없는 디폴트 생성자도 함께 정의해주는 것을 잊지 말자.
	public User() {
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Level getLevel() {
		return level;
	}
	public void setLevel(Level level) {
		this.level = level;
	}
	public int getLogin() {
		return login;
	}
	public void setLogin(int login) {
		this.login = login;
	}
	public int getRecommend() {
		return recommend;
	}
	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}
}