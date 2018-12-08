package springbook.user.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

//트랜잭션 InvocationHandler
//다이내믹 프록시를 위한 트랜잭션 부가기능.
public class TransactionHandler implements InvocationHandler {
	private Object target; //부가기능을 제공할 타깃 오브젝트. 어떤 타입의 오브젝트에도 적용 가능.
	private PlatformTransactionManager transactionManager; //트랜잭션 기능을 제공하는 데 필요한 트랜잭션 매니저 
	private String pattern; //트랜잭션을 적용할 메소드 이름패턴
	
	public void setTarget(Object target) {
		this.target = target;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if(method.getName().startsWith(pattern)){ //트랜잭션 적용 대상 메소드를 선별해서 트랜잭션 경계설정 기능을 부여해준다.
			return invokeTransaction(method, args);
		}else{
			return method.invoke(target, args);
		}
	}

	private Object invokeTransaction(Method method, Object[] args) throws Throwable {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition()); 
		try {
			//트랜잭션을 시작하고 타깃 오브젝트의 메소드를 호출. 예외가 발생하지 않았다면 커밋. 
			Object ret = method.invoke(target, args);
			this.transactionManager.commit(status);
			return ret;
		} catch (InvocationTargetException e) { 
			//cf. 리플렉션 메소드인 Method.invoke()를 이용해 타킷 오브젝트의 메소드를 호출할 때는
			//타킷 오브젝트에서 발생하는 예외가 InvocationTargetException으로 한번 더 포장돼서 전달된다. 
			//따라서 일단 InvocationTargetException으로 받은 후 getTargetException() 메소드로 중첩되어 있는 예외를 가져와야 한다.
			
			this.transactionManager.rollback(status); //예외가 발생하면 트랜잭션을 롤백.
			throw e.getTargetException();
		}
	}
}
