package springbook.user.sqlservice;
/**
 * SQL을 제공받아 등록해뒀다가 키로 검색해서 돌려주는 기능을 담당. 
 */
public interface SqlRegistry {
	/**
	 * SQL을 키와 함께 등록한다.
	 * @param key
	 * @param sql
	 */
	void registerSql(String key, String sql); 
	
	/**
	 * 키로 SQL을 검색. 검색에 실패하면 예외를 던짐.
	 * @param key
	 * @return
	 * @throws SqlNotFoundException
	 */
	String findSql(String key) throws SqlNotFoundException;
}
