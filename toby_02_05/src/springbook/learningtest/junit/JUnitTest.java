package springbook.learningtest.junit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.junit.matchers.JUnitMatchers.either;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

// 스프링의 테스트용 애플리케이션 컨텍스트가 한 개만 만들어져, 모든 테스트 메소드에서 공유되는지 확인하기 위한 테스트  
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/springbook/learningtest/junit/junit.xml")
public class JUnitTest {
	@Autowired 
	ApplicationContext context; //테스트 컨텍스트가 매번 주입해주는 애플리케이션 컨텍스트는 항상 같은 오브젝트인지 확인.
	
	static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
	static ApplicationContext contextObject = null;
	
	@Test
	public void test1(){
		//is()는 equals() 비교를해서 같으면 성공이지만, is(not())은 반대로 같지 않아야 성공.
		//hasItem()는 실제로 같은 오브젝트인지를 비교.
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
					
		assertThat(contextObject == null || contextObject == this.context, is(true)); //cf.is()는 타입만 일치하면 어떤 값이든 검증 가능.
		contextObject = this.context;
	}
	
	@Test
	public void test2(){
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		
		assertTrue(contextObject == null || contextObject == this.context);
		contextObject = this.context;
	}

	@Test
	public void test3(){
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
		//either()는 뒤에 이어서 나오는 or()와 함께 두 개의 매처의 결과를 OR조건으로 비교. 두 가지 매처 중에서 하나만 true로 나와도 성공. 
		assertThat(contextObject, either(is(nullValue())).or(is(this.context)));
	}
	
}
