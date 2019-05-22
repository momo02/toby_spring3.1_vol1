package springbook.learningtest.spring.oxm;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;

import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

// OXM (Object-XML Mappring) 언마샬링 (xml -> object) 테스트
public class OxmTest {
	@Autowired Unmarshaller unmarshaller;
	
	public void unmarshallSqlMap() throws XmlMappingException, IOException {
		//InputStream을 이용하는 Source타입의 StreamSource를 생성.
		Source xmlSource = new StreamSource(getClass().getResourceAsStream("sqlmap.xml"));
		
		//// 어떤 OXM 기술이든 언마샬은 이 한줄이면 끝!
		Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(xmlSource);
		
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
