package springbook.learningtest.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

//다이내믹 프록시로부터 요청을 전달받으려면 InvocationHandler를 구현해야 한다.
//다이내믹 프록시 오브젝트는 클라이언트의 모든 요청을 리플랙션 정보로 변환해서 
//InvocationHandler 구현 오브젝트의 invoke() 메소드로 넘긴다.
public class UppercaseHandler implements InvocationHandler {
	Hello target; 
	//다이내믹 프록시로부터 전달받은 요청을 다시 타킷 오브젝트에 위임해야 하기 때문에 타킷 오브젝트를 주입받아 둔다.
	public UppercaseHandler(Hello target) {
		this.target = target; 
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//타깃 오브젝트의 메소드 호출(타깃으로 위임). 인터페이스의 메소드 호출에 모두 적용된다.
		String ret = (String)method.invoke(target, args); //Hello 인터페이스의 모든 메소드는 결과가 String타입이므로 메소드 호출의 결과를 String타입으로 변환해도 안전.
		return ret.toUpperCase(); //부가기능 제공.(리턴 값을 대문자로 바꾸는 작업 수행)
	}
}
