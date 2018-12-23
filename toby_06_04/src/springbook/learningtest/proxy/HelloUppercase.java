package springbook.learningtest.proxy;

//프록시 클래스
//데코레이터 패턴을 적용해 타깃인 HelloTarget에 부가기능을 추가
//( 리턴하는 문자를 모두 대문자로 바꿔주는 기능 추가 )
public class HelloUppercase implements Hello {
	Hello hello; //위임할 타깃 오브젝트. HelloTarget 타깃클래스의 오브젝트인 것은 알지만
				 //다른 프록시를 추가할 수도 있으므로 인터페이스로 접근한다.
	
	public HelloUppercase(Hello hello) {
		this.hello = hello;
	}
	
	public String sayHello(String name) {
		return hello.sayHello(name).toUpperCase(); //타깃 오브젝트에 요청을 위임 + 부가기능 적용
	}

	public String sayHi(String name) {
		return hello.sayHi(name).toUpperCase();
	}

	public String sayThankyou(String name) {
		return hello.sayThankyou(name).toUpperCase();
	}
	
	//==> 이 프록시는 프록시 적용의 일반적인 문제점 2가지를 모두 갖고 있다.
	// 인터페이스의 모든 메소드를 구현해 위임하도록 코드를 만들어야 하며,
	// 부가기능인 리턴 값을 대문자로 바꾸는 기능이 모든 메소드에 중복돼서 나타난다.
}
