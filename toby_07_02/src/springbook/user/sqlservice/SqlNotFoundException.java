package springbook.user.sqlservice;

public class SqlNotFoundException extends RuntimeException {
	public SqlNotFoundException(String message) {
		super(message);
	}
	
	//Sql을 가져오는 데 실패한 근본 원인을 담을 수 있도록 중첩 예외를 저장할 수 있는 생성자를 만들어둔다.
	public SqlNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
