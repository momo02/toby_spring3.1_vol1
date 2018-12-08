package springbook.learningtest.jdk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

//리플렉션 학습 테스트
public class ReflectionTest {
	@Test
	public void invokeMethod() throws Exception {
		String name = "Spring";
		
		// length()
		assertThat(name.length(), is(6));
		// Method를 이용해 리플렉션 방식으로 호출.
		Method lengthMethod = String.class.getMethod("length"); //"length"라는 이름의, 파라미터 없는 메소드의 정보를 가져옴.
		assertThat((Integer)lengthMethod.invoke(name), is(6));
		
		// charAt()
		assertThat(name.charAt(0), is('S'));
		// Method를 이용해 리플렉션 방식으로 호출.
		Method charAtMethod = String.class.getMethod("charAt", int.class); //"charAt"라는 이름의, int타입 파라미터를 가지는 메소드의 정보를 가져옴.
		assertThat((Character)charAtMethod.invoke(name, 0), is('S'));
	}
}
