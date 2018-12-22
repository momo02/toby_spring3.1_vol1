package springbook.learningtest.spring.factorybean;

//생성자를 제공하지 않는 클래스
//=> Message클래스의 오브젝트를 만들려면 newMessage()라는 스태틱 메소드를 사용해야 함.
//	일반적으로 private 생성자를 가진 클래스를 스프링 빈으로 등록하는 것은 권장되지 않으며, 등록하더라도 빈 오브젝트가 바르게 동작하지 않을 가능성이 있으니 주의!
public class Message {
	String text;
	
	//생성자가  private으로 선언되어 있어서 외부에서 생성자를 통해 오브젝트를 만들 수 없다. 
	private Message(String text){
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	//생성자 대신 사용할 수 있는 스태틱 팩토리 메소드를 제공.
	public static Message newMessage(String text){
		return new Message(text);
	}
}
