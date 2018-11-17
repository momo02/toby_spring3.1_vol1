package springbook.user.domain;

//사용자 레벨용 enum
// Level 이늄은 내부에서는 DB에 저장할 int타입의 값을 갖고 있지만, 겉으로는 Level타입의 오브젝트이기 때문에 안전하게 사용할 수 있다. 
// user1.setLevel(1000)과 같은 코드는 컴파일러가 타입이 일치하지 않는다는 에러를 내면서 걸러줄 것이다. 
public enum Level {
	BASIC(1), SILVER(2), GOLD(3); //3개의 enum 오브젝트 정의 
	
	private final int value;
	 
	Level(int value) { //DB에 저장할 값을 넣어줄 생성자를 만들어둔다 
		this.value = value;
	}
	
	public int intValue(){ //값을 가져오는 메소드
		return value;
	}
	
	public static Level valueOf(int value) { //값으로부터 Level 타입 오브젝트를 가져오도록 만든 static 메소드 
		switch(value) {
			case 1 : return BASIC;
			case 2 : return SILVER;
			case 3 : return GOLD;
			default : throw new AssertionError("Unknown value: " + value);
		}
	}
}
