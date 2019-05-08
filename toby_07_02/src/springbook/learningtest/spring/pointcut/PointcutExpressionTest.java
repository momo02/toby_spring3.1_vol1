package springbook.learningtest.spring.pointcut;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

public class PointcutExpressionTest {
	
	@Test
	public void methodSignaturePointcut() throws NoSuchMethodException, SecurityException {
		
		//Target 클래스 minus()메소드의 풀 시그니처 출력
		System.out.println(Target.class.getMethod("minus", int.class, int.class));
		// ==> public int springbook.learningtest.spring.pointcut.Target.minus(int,int) throws java.lang.RuntimeException
		
		//메소드 시그니처를 이용한 포인트컷 표현식 Test
		//-> 포인트컷 표현식은 메소드 시그니처를 execution() 안에 넣어서 작성. 메소드 실행에 대한 포인트컷이라는 의미.
//		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
//		pointcut.setExpression("execution(public int " + 
//				"springbook.learningtest.spring.pointcut.Target.minus(int,int) " + 
//				"throws java.lang.RuntimeException)");
		
		//필수가 아닌 항목(접근제한자, 클래스 타입, 예외 패턴)을 생략하여 좀 더 간결하게 정리
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(int minus(int,int)");
		
		//Target.minus() 
		//클래스 필터와 메소드 매처를 가져와 각각 비교
		assertThat(pointcut.getClassFilter().matches(Target.class) && 
				  pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class,int.class), null), is(true)); //포인트컷 조건 통과
		
		//Target.plus() 
		assertThat(pointcut.getClassFilter().matches(Target.class) && 
				  pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class,int.class), null), is(false)); //메소드 매처에서 실패
		
		//Bean.method()
		assertThat(pointcut.getClassFilter().matches(Bean.class) && 
				  pointcut.getMethodMatcher().matches(Bean.class.getMethod("method"), null), is(false)); //클래스 필터에서 실패
		
	}
	
	//포인트컷과 메소드를 비교해주는 테스트 헬퍼 메소드
	public void pointcutMatches(String expression, Boolean expected, Class<?> clazz, String methodName, Class<?>... args) throws Exception {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(expression);
		//포인트컷의 클래스 필터와 메소드 매처 두 가지를 동시에 만족하는지 확인.
		assertThat(pointcut.getClassFilter().matches(clazz) 
				&& pointcut.getMethodMatcher().matches(clazz.getMethod(methodName, args), null), is(expected));
	}
	
	//타깃 클래스의 메소드 6개에 대해 포인트컷 선정 여부를 검사하는 헬퍼 메소드
	public void targetClassPointcutMatches(String expression, boolean... expected) throws Exception {
		
		pointcutMatches(expression, expected[0], Target.class, "hello");
		pointcutMatches(expression, expected[1], Target.class, "hello", String.class);
		pointcutMatches(expression, expected[2], Target.class, "plus", int.class, int.class);
		pointcutMatches(expression, expected[3], Target.class, "minus", int.class, int.class);
		pointcutMatches(expression, expected[4], Target.class, "method");
		pointcutMatches(expression, expected[5], Bean.class, "method");
	}
	
	@Test
	public void pointcut() throws Exception {
		//모든 메소드를 다 허용하는 표현식 --> 모든 메소드에 대해 true
		targetClassPointcutMatches("execution(* *(..))" , true, true, true, true, true, true);
	}
}
