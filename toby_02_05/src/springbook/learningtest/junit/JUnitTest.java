package springbook.learningtest.junit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JUnitTest {
	static JUnitTest testObject;
	
	@Test
	public void test1(){
		//is()는 equals() 비교를해서 같으면 성공이지만, is(not())은 반대로 같지 않아야 성공.
		//sameInstance()는 실제로 같은 오브젝트인지를 비교.
		assertThat(this, is(not(sameInstance(testObject))));
		testObject = this;
	}
	
	@Test
	public void test2(){
		assertThat(this, is(not(sameInstance(testObject))));
		testObject = this;
	}

	@Test
	public void test3(){
		assertThat(this, is(not(sameInstance(testObject))));
		testObject = this;
	}
	
}
