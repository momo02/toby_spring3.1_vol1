package springbook.learningtest.jdk.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;



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
	
	@Test
	public void proxyFactoryBean() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget()); 
		pfBean.addAdvice(new UppercaseAdvice()); //부가기능을 담은 어드바이스를 추가. 여러개 추가 가능.
		
		//FactoryBean이므로 getObject()로 생성된 프록시를 가져온다.
		Hello proxiedHello = (Hello) pfBean.getObject();
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankyou("Toby"), is("THANK YOU TOBY"));
	}
	
	static class UppercaseAdvice implements MethodInterceptor {
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			//리플랙션의 Method와 달리 메소드 실행 시 타킷 오브젝트를 전달할 필요가 없다.
			//MethodInvocation은 메소드 정보와 함께 타킷 오브젝트를 알고 있기 때문.
			String ret = (String) invocation.proceed(); //타킷 오브젝트의 메소드를 내부적으로 실행.
			return ret.toUpperCase(); //부가기능 적용 
		}
	}
	
	//포인트컷까지 적용한 ProxyFactoryBean
	@Test
	public void pointcutAdvisor() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());

		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*"); //이름 비교조건 설정. sayH로 시작하는 모든 메소드를 선택
		
		//포인트컷과 어드바이스를 Advisor로 묶어서 한 번에 추가
		//cf. 어드바이저 = 포인트컷(메소드 선정 알고리즘) + 어드바이스(부가기능)
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		
		Hello proxiedHello = (Hello)pfBean.getObject();
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		//메소드 명이 포인트컷의 선정조건에 맞지 않아 부가기능(대문자변환)이 적용 X.
		assertThat(proxiedHello.sayThankyou("Toby"), is("Thank You Toby"));
	}
	
}
