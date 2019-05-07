package springbook.user.sqlservice;

public interface SqlService {
	
	//sql에 대한 키 값을 전달하면 그에 해당하는 sql을 돌려준다.
	String getSql(String key) throws SqlRetrievalFailureException;
}
