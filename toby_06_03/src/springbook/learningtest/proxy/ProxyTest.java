package springbook.learningtest.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ProxyTest {
	@Test
	public void simpleProxy(){
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
}
