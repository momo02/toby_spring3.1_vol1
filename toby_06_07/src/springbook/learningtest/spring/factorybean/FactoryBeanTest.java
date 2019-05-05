package springbook.learningtest.spring.factorybean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration //설정파일 이름을 지정하지 않으면 클래스이름 + "-context.xml"이 디폴트로 사용된다. ( + 동일한 path에 위치해야함)
public class FactoryBeanTest {
	@Autowired
	ApplicationContext context;
	
	//팩토리 빈 테스트
	@Test
	public void getMessageFromFactoryBean() {
		//cf. message 빈의 타입이 무엇인지 확실치 않으므로 @Autowired으로 빈을 가져오는 대신
		// 	  ApplicationContext를 이용해 getBean() 메소드를 사용.
		Object message = context.getBean("message");
		
		//타입 확인
		//==> message 빈 설정의 class 애트리뷰트는 MessageFactoryBean이지만 getBean()이 리턴한 오브젝트는 Message타입이여야 함.
		assertThat( message, is(Message.class) ); 
		
		//설정과 기능 확인
		//==> MessageFactoryBean을 통해 text프로터티의 값이 바르게 주입됐는지 확인.
		assertThat( ((Message)message).getText(), is("Factory Bean") ); 
		
		//==> ★ FactoryBean 인터페이스를 구현한 클래스를 스프링 빈으로 만들어두면 getObject()라는 메소드가 생성해주는 오브젝트가 실제 빈의 오브젝트로 대치된다!! 
	}
	
	//팩토리 빈을 가져오는 기능 테스트
	@Test
	public void getFactoryBean() {
		//드물지만 팩토리 빈이 만들어주는 빈 오브젝트가 아니라 팩토리 빈 자체를 가져오고 싶을 경우,
		// '&'를 빈 이름 앞에 붙여주면 팩토리 빈 자체를 돌려준다.
		Object factory = context.getBean("&message");
		assertThat(factory, is(MessageFactoryBean.class));
	}
}