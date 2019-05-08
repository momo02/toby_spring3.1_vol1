package springbook.user.sqlservice;

/**
 * SQL정보를 외부의 리소스로부터 읽어와서, 애플리케이션 내의 저장소(SqlRegistry)에 등록하는 기능을 담당.
 * => SqlReader는 SqlRegistry 오브젝트를 메소드 파라미터로 DI 받아 
 *    읽어들인 SQL을 등록하는 데 사용하도록 만든다.
 */
public interface SqlReader {
	void read(SqlRegistry sqlRegistry);
}
