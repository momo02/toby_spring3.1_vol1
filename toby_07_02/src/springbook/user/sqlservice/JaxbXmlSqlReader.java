package springbook.user.sqlservice;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import springbook.user.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class JaxbXmlSqlReader implements SqlReader {
	//new :: sqlmapFile 프로퍼티에 디폴트 값을 준다.(상수) 
	private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
	
	private String sqlmapFile = DEFAULT_SQLMAP_FILE; //sql을 담은 XML파일의 위치와 이름 
	//sqlmapFile은 SqlReader의 특정 구현 방법에 종속되는 프로퍼티.
	
	//sqlmapFile을 지정하면 지정된 파일이 사용되고, 아니라면 디폴트로 넣은 파일명이 사용됨.
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}

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

}
