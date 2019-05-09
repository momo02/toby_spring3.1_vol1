package springbook.user.sqlservice;

/**
  7.2.6 디폴트 의존관계 
  디폴트 의존관계 (외부에서 DI 받지 않는 경우 기본적으로 자동 적용되는 의존관계) 를 갖는 빈 만들기 
  => 특정 의존 오브젝트가 대부분의 환경에서 거의 디폴트라고 해도 좋을 만큼 기본적으로 사용될 가능성이 있을 경우 
     디폴트 의존관계를 갖는 빈을 만드는 것을 고려.
 */
public class DefaultSqlService extends BaseSqlService {
	//생성자를 통한 디폴트 의존관계 설정
	public DefaultSqlService() {
		//생성자에서 디폴트 의존 오브젝트(DI설정이 없을 경우 디폴트로 적용하고 싶은 의존 오브젝트)를 직접 만들어서 스스로 DI 해준다.
		setSqlReader(new JaxbXmlSqlReader());
		setSqlRegistry(new HashMapSqlRegistry());
	}
}
