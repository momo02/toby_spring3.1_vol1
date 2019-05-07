package springbook.user.sqlservice;

import java.util.Map;

//스프링 설정을 사용하는 단순 SQL 서비스
public class SimpleSqlService implements SqlService{
	
	private Map<String, String> sqlMap;
	
	public Map<String, String> getSqlMap() {
		return sqlMap;
	}

	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}

	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		String sql = sqlMap.get(key); 
		//인터페이스에 정의된 규약대로 SQL을 가져오는 데 실패하면 예외를 던지게 함.
		if(sql == null) {
			throw new SqlRetrievalFailureException(key + "에 대한 SQL을 찾을 수 없습니다.");
		}
		return sql;
	}
}
