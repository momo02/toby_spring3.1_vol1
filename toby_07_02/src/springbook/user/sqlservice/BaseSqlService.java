package springbook.user.sqlservice;

import javax.annotation.PostConstruct;

/* 
 new :: 7.2.6 디폴트 의존관계
- 확장 가능한 기반 클래스
*/
public class BaseSqlService implements SqlService {
	//BaseSqlService는 상속을 통해 확장해서 사용하기에 적합.
	//서브클래스에서 필요한 경우 접근할 수 있도록 protected로 선언.
	protected SqlReader sqlReader;
	protected SqlRegistry sqlRegistry;
	
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}

	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	@PostConstruct
	public void loadSql() {
		this.sqlReader.read(this.sqlRegistry);
	}
	
	public String getSql(String key) throws SqlRetrievalFailureException {
		try {
			return this.sqlRegistry.findSql(key);
		} 
		catch(SqlNotFoundException e) {  // SqlRegistry에서 발생하는 예외를 SqlService 인터페이스에서 정의한 예외로 전환
			throw new SqlRetrievalFailureException(e);
		}
	}
}
