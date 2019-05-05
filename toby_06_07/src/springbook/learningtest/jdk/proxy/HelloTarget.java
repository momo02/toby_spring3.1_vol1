package springbook.learningtest.jdk.proxy;

public class HelloTarget implements Hello{

	public String sayHello(String name) {
		return "Hello " + name;
	}

	public String sayHi(String name) {
		return "Hi " + name;
	}

	public String sayThankyou(String name) {
		return "Thank You " + name;
	}

}
