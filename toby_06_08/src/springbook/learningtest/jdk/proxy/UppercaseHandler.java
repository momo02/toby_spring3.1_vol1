package springbook.learningtest.jdk.proxy;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

// new :: 확장된 UppercaseHandler
// -> 어떤 종류의 인터페이스를 구현한 타깃이든 상관없이 재사용할 수 있고, 메소드의 리턴타입이 String인 경우만 대문자로 결과를 바꿔주도록 확장.

//다이내믹 프록시로부터 요청을 전달받으려면 InvocationHandler를 구현해야 한다.
//다이내믹 프록시 오브젝트는 클라이언트의 모든 요청을 리플랙션 정보로 변환해서 InvocationHandler 구현 오브젝트의 invoke() 메소드로 넘긴다.
public class UppercaseHandler implements InvocationHandler {
	Object target;  //어떤 종류의 인터페이스를 구현한 타깃에도 적용 가능하도록 Object 타입으로 수정.
	//다이내믹 프록시로부터 전달받은 요청을 다시 타킷 오브젝트에 위임해야 하기 때문에 타킷 오브젝트를 주입받아 둔다.
	public UppercaseHandler(Object target) {
		this.target = target; 
	}
	
	/* InvocationHandler는 단일 메소드에서 모든 요청을 처리하기 때문에, 어떤 메소드에 어떤 기능을 적용할지를 선택하는 과정이 필요할 수도 있음.
	 * 호출하는 메소드의 이름, 파라미터의 개수와 타입, 리턴 타입 등의 정보를 가지고 부가적인 기능을 적용할 메소드를 선택할 수 있다. 
	 * 
	 * if.. 메소드 이름이 "say"로 시작하는 경우에만 대문자로 바꾸는 기능을 적용하고 싶다면 아래와 같이 Method파라미터에서 메소드 이름을 가져와 확인하는 방법을 사용.
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//타깃 오브젝트의 메소드 호출(타깃으로 위임). 인터페이스의 메소드 호출에 모두 적용된다.
		Object ret = method.invoke(target, args);
		if(ret instanceof String && method.getName().startsWith("say")) { //리턴 타입과 메소드 이름이 일치하는 경우에만 부가기능을 적용.
			return ((String)ret).toUpperCase();
		}else{
			return ret; //조건이 일치하지않으면 타킷 오브젝트의 호출 결과를 그대로 리턴.
		}
	}
}
