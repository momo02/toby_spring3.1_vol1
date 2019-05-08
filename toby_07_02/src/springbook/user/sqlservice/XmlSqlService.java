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

//생성자 초기화 방법을 사용하는 XmlSqlService
// => 생성자에서 JAXB를 이용해 XML로 된 SQL 문서를 읽어들이고, 변환된 Sql오브젝트들을 맵으로 옮겨 저장해뒀다가 DAO의 요청에 따라 SQL을 찾아서 전달하는 방식
public class XmlSqlService implements SqlService {
	
	private Map<String, String> sqlMap = new HashMap<String, String>(); 
	
	//new :: sql을 담은 XML파일의 위치와 이름이 코드에 고정되지않고, 
	//외부에서 DI로 설정해줄 수 있도록 변경
	private String sqlmapFile;

	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}

	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		String sql = sqlMap.get(key);
		if(sql == null) {
			throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
		}else 
			return sql;
	}
	
	//new :: 생성자 대신 사용할 초가화 메소드
	//	=> 생성자에서 예외가 발생할 수도 있는 복잡한 초기화 작업을 다루는것은 좋지 않다.
	//     생성자에서 발생하는 예외는 다루기 힘들고, 상속하기 불편하며, 보안에도 문제가 생길 수 있음.
	
	// @PostConstruct -> 빈의 초기화 메소드로 지정
	// 스프링은 빈의 오브젝트를 생성하고 DI작업을 마친 뒤에 @PostConstruct가 붙은 메소드를 자동으로 실행해준다. 
	// 생성자와는 달리 프로퍼티까지 모두 준비된 후에 실행된다는 면에서 매우 유용!
	@PostConstruct
	public void loadSql() {
		String contextPath = Sqlmap.class.getPackage().getName();
		try {
			//JAXB API를 이용해 XML문서를 오브젝트 트리로 읽어온다.
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile); //UserDao와 같은 클래스 패스의 sqlmap.xml파일을 변환.
			Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
			
			//읽어온 SQL을 맵으로 저장해둔다.
			for(SqlType sql : sqlmap.getSql()) {
				sqlMap.put(sql.getKey(), sql.getValue());
			}
		}catch(JAXBException e) {
			//JAXBException은 복구 불가능한 예외. 불필요한 throws를 피하도록 런타임 예외로 포장해 던짐.
			throw new RuntimeException(e);
		}
	}
}
