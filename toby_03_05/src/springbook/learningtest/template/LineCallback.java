package springbook.learningtest.template;

//제네릭스를 이용한 콜백 인터페이스
/* cf.제네릭-> 클래스 내부에서 사용할 데이터 타입을 나중에 인스턴스를 생성할 때 확정함.
 */
public interface LineCallback<T> { //타입 파라미터를 적용한 LineCallback
	T doSomethingWithLine(String line, T value);
}
