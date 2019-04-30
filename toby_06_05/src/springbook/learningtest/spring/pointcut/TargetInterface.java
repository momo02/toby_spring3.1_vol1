package springbook.learningtest.spring.pointcut;

public interface TargetInterface {
	void hello();
	void hello(String a);
	int minus(int a, int b) throws RuntimeException;
	public int plus(int a, int b);
}
