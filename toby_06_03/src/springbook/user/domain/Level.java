package springbook.user.domain;

//사용자 레벨용 enum
// Level 이늄은 내부에서는 DB에 저장할 int타입의 값을 갖고 있지만, 겉으로는 Level타입의 오브젝트이기 때문에 안전하게 사용할 수 있다. 
// user1.setLevel(1000)과 같은 코드는 컴파일러가 타입이 일치하지 않는다는 에러를 내면서 걸러줄 것이다. 
public enum Level {
	//업그레이드 순서를 담고 있도록 수정한 Level
	GOLD(3, null), SILVER(2, GOLD), BASIC(1, SILVER);
	
	private final int value;
	private final Level next; //다음 단계의 레벨 정보를 스스로 갖고 있도록 Level타입의 next변수를 추가. 
	
	Level(int value, Level next) { 
		this.value = value;
		this.next = next;
	}
	
	public int intValue(){ //int 값을 가져오는 메소드
		return value;
	}
	
	public Level nextLevel(){
		return this.next;
	}
	
	public static Level valueOf(int value) { //int 값으로부터 Level 타입 오브젝트를 가져오도록 만든 static 메소드 
		switch(value) {
			case 1 : return BASIC;
			case 2 : return SILVER;
			case 3 : return GOLD;
			default : throw new AssertionError("Unknown value: " + value);
		}
	}
}
