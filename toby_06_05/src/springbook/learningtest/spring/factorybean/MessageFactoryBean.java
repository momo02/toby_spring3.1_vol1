package springbook.learningtest.spring.factorybean;

import org.springframework.beans.factory.FactoryBean;
/*cf.	'팩토리 빈'이란 스프링을 대신해 오브젝트의 생성로직을 담당하도록 만들어진 특별한 빈.
 		팩토리빈을 만드는 가장 간단한 방법은 스프링의 FactoryBean이라는 인터페이스를 구현하는 것.
		스프링은 FactoryBean 인터페이스를 구현한 클래스가 빈의 클래스로 지정되면, 
		팩토리 빈 클래스 오브젝트의 getObject()메소드를 이용해 오브젝트를 가져오고, 이를 빈 오브젝트로 사용. 
		빈 의 클래스로 등록된 팩토린 빈은 오브젝트를 생성하는 과정에서만 사용될 뿐이다.
*/

// Message의 팩토리 빈 클래스(Message클래스의 오브젝트를 생성해준다)
public class MessageFactoryBean implements FactoryBean<Message> {
	String text;
	
	public void setText(String text) {
		this.text = text;
	}
	//==> 오브젝트를 생성할 때 필요한 정보를 팩토리 빈의 프로퍼티로 설정해서 대신 DI받을 수 있게 한다.
	// 	  주입된 정보는 오브젝트 생성 중에 사용된다.
	
	//실제 빈으로 사용될 오브젝트를 직접 생성.
	//코드를 이용하기 때문에 복잡한 방식의 오브젝트 생성과 초기화 작업도 가능.
	@Override
	public Message getObject() throws Exception {
		return Message.newMessage(text);
	}

	@Override
	public Class<?> getObjectType() {
		return Message.class;
	}
	
	//getObject() 메소드가 돌려주는 오브젝트가 싱글톤인지를 알려준다. 
	//이 팩토리 빈은 매번 요청할 때마다 새로운 오브젝트를 만들므로 false로 설정.
	//이것은 팩토리 빈의 동작방식에 관한 설정이고, 만들어진 빈 오브젝트는 싱글톤으로 스프링이 관리해줄 수 있다.
	@Override
	public boolean isSingleton() {
		return false;
	}
}