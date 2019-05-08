package springbook.learningtest.jdk.jaxb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

//Jaxb 학습 테스트
public class JaxbTest {
	
	//sqlmap.xml이 JAXB 언마샬링을 통해 매핑 오브젝트로 변환되는지 확인
	@Test
	public void readSqlmap() throws JAXBException, IOException {
		String contextPath = Sqlmap.class.getPackage().getName();
		//바인딩용 클래스들 위치를 가지고 JAXB 컨텍스트를 생성.
		JAXBContext context = JAXBContext.newInstance(contextPath);
		//언마샬러 생성
		Unmarshaller unmarshaller = context.createUnmarshaller(); 
		
		//cf. XML문서를 읽어서 자바의 오브젝트로 변환하는 것을 JAXB에서는 언마샬링(unmarshalling)이라 함.
		//언마샬을 하면 매핑된 오브젝트 트리의 루트인 Sqlmap을 돌려준다.
		Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal( 
				getClass().getResourceAsStream("sqlmap.xml"));
		
		List<SqlType> sqlList = sqlmap.getSql();
		
		assertThat(sqlList.size(), is(3));
		assertThat(sqlList.get(0).getKey(), is("add"));
		assertThat(sqlList.get(0).getValue(), is("insert"));
		assertThat(sqlList.get(1).getKey(), is("get"));
		assertThat(sqlList.get(1).getValue(), is("select"));
		assertThat(sqlList.get(2).getKey(), is("delete"));
		assertThat(sqlList.get(2).getValue(), is("delete"));
	}
}
