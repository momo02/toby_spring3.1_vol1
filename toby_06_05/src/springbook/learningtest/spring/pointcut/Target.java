package springbook.learningtest.spring.pointcut;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;

//포인트컷 테스트용 클래스
public class Target implements TargetInterface {
	public void hello() {}
	public void hello(String a) {}
	public int minus(int a, int b) throws RuntimeException { return 0; }
	public int plus(int a, int b) { return 0; }
	public void method() {}
}
