package springbook.learningtest.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.junit.Test;

public class ProxyTest {
	@Test
	public void simpleProxy() {
		Hello hello = new HelloTarget(); //타깃은 인터페이스를 통해 접근.
		assertThat(hello.sayHello("Toby"), is("Hello Toby"));
		assertThat(hello.sayHi("Toby"), is("Hi Toby"));
		assertThat(hello.sayThankyou("Toby"), is("Thank You Toby"));
	
		//HelloUppercase 프록시 테스트
		Hello proxiedHello = new HelloUppercase(new HelloTarget());//프록시를 통해 타깃 오브젝트에 접근하도록 구성.
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankyou("Toby"), is("THANK YOU TOBY"));
	}
	
	@Test
	public void dynamicProxy() {
		//다이내믹 프록시 생성 
		//생성된 다이내믹 프록시 오브젝트는 Hello 인터페이스를 구현하고 있으므로 Hello타입으로 캐스팅해도 안전.
		Hello proxiedHello = (Hello)Proxy.newProxyInstance(
				getClass().getClassLoader(), //동적으로 생성되는 다이내믹 프록시 클래스의 로딩에 사용할 클래스 로더
				new Class[] { Hello.class }, //구현할 인터페이스 -> 다이내믹 프록시는 한 번에 하나 이상의 인터페이스를 구현할 수도 있다. 따라서 인터페이스의 배열을 사용.
				new UppercaseHandler(new HelloTarget())); //부가기능과 위임 코드를 담은 InvocationHandler
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankyou("Toby"), is("THANK YOU TOBY"));
	}
}
