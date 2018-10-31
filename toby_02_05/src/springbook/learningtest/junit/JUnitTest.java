package springbook.learningtest.junit;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/* 테스트마다 현재 테스트 오브젝트가 컬렉션에 이미 등록되어 있는지 확인하고, 없으면 자기 자신을 추가. 
   ->테스트가 어떤 순서로 실행되는지에 상관없이 오브젝트 중복 여부를 확인 가능. */
public class JUnitTest {
	static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
	
	@Test
	public void test1(){
		//is()는 equals() 비교를해서 같으면 성공이지만, is(not())은 반대로 같지 않아야 성공.
		//hasItem()는 실제로 같은 오브젝트인지를 비교.
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
	}
	
	@Test
	public void test2(){
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
	}

	@Test
	public void test3(){
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
	}
	
}
