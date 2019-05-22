package springbook.user.sqlservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import springbook.user.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

/* 
 7.2.5 자기참조 빈으로 시작하기
- 다중 인터페이스 구현과 간접 참조 : XmlSql클래스 하나가 SqlService, SqlReader, SqlRegistry 라는 세 개의 인터페이스를 구현,
  같은 클래스 코드이지만 책임이 다른 코드는 직접 접근하지 않고 인터페이스를 통해 간접적으로 사용. 
  ==> 같은 클래스 안에 구현된 내용이지만 SqlService의 메소드에서 Sql을 읽을 때는 SqlReader인터페이스를 통해, 
      SQL을 찾을 때는 SqlRegistry 인터페이스를 통해 간접적으로 접근하게 함. 
*/
public class XmlSqlService implements SqlService, SqlRegistry, SqlReader {
	
	private SqlReader sqlReader;
	private SqlRegistry sqlRegistry;
	
	//sqlmapFile은 SqlReader 구현의 일부가 됨. 따라서 SqlReader 구현 메소드를 통하지 않고는 접근 X. 
	private String sqlmapFile; //sql을 담은 XML파일의 위치와 이름
	
	//sqlMap은 이제 SqlRegistry 구현의 일부가 됐으므로 SqlRegistry 구현 메소드가 아닌 메소드에서는 직접 사용 X.
	//독립적인 오브젝트라 생각하고 SqlRegistry의 메소드를 통해 접근
	private Map<String, String> sqlMap = new HashMap<String, String>(); 
	
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}

	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}
	
	//--------------- SqlRegistry 구현 ---------------
	//HashMap이라는 저장소를 사용하는 구체적인 구현 방법에서 독립될 수 있도록 인터페이스의 메소드로 접근하게 해준다.
	@Override
	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if(sql == null) {
			throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
		}else 
			return sql;
	}
	//------------------------------------------------
	

	//--------------- SqlReader 구현 ------------------
	//loadSql()에 있던 코드를 SqlReader 메소드로 가져온다. 초기화를 위해 무엇을 할 것인가와 SQL을 어떻게 읽는지를 분리.
	@Override
	public void read(SqlRegistry sqlRegistry) {
		String contextPath = Sqlmap.class.getPackage().getName();
		try {
			//JAXB API를 이용해 XML문서를 오브젝트 트리로 읽어온다.
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile); //UserDao와 같은 클래스 패스의 sqlmap.xml파일을 변환.
			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);

			for(SqlType sql : sqlmap.getSql()) {
				sqlRegistry.registerSql(sql.getKey(), sql.getValue());
			}
			
		}catch(JAXBException e) {
			//JAXBException은 복구 불가능한 예외. 불필요한 throws를 피하도록 런타임 예외로 포장해 던짐.
			throw new RuntimeException(e);
		}
	}
	//------------------------------------------------
	
	//--------------- SqlService 구현 ------------------
	//생성자 대신 사용할 초가화 메소드
	//	=> 생성자에서 예외가 발생할 수도 있는 복잡한 초기화 작업을 다루는것은 좋지 않다.
	//     생성자에서 발생하는 예외는 다루기 힘들고, 상속하기 불편하며, 보안에도 문제가 생길 수 있음.
	
	// @PostConstruct -> 빈의 초기화 메소드로 지정
	// 스프링은 빈의 오브젝트를 생성하고 DI작업을 마친 뒤에 @PostConstruct가 붙은 메소드를 자동으로 실행해준다. 
	// 생성자와는 달리 프로퍼티까지 모두 준비된 후에 실행된다는 면에서 매우 유용!
	@PostConstruct
	public void loadSql() {
		this.sqlReader.read(this.sqlRegistry);
	}
	
	// getSql()은 내부 전략인 SqlRegistry타입 오브젝트에게 요청해서 SQL을 가져오게 하고,
	// SqlRegistry에서 발생하는 예외를 SqlService 인터페이스에서 정의한 예외로 전환해주는 기능을 구현.
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		try {
			return this.sqlRegistry.findSql(key);
		} 
		catch(SqlNotFoundException e) {
			throw new SqlRetrievalFailureException(e);
		}
	}
	//------------------------------------------------
}
