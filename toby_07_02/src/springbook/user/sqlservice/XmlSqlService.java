package springbook.user.sqlservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;

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
	
	//스프링이 오브젝트를 만드는 시점에서 SQL을 읽어오도록 생성자를 이용.
	public XmlSqlService() {
		String contextPath = Sqlmap.class.getPackage().getName();
		try {
			//JAXB API를 이용해 XML문서를 오브젝트 트리로 읽어온다.
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream("sqlmap.xml"); //UserDao와 같은 클래스 패스의 sqlmap.xml파일을 변환.
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

	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		String sql = sqlMap.get(key);
		if(sql == null) {
			throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
		}else 
			return sql;
	}
}
