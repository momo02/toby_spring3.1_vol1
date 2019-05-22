package springbook.user.service;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/* 테스트용 메일 발송 오브젝트
   
   스프링이 제공한 메일 전송 기능에 대한 인터페이스인 MailSender가 있으니 이를 구현해 테스트용 메일 전송 클래스를 만든다.
   
   테스트가 수행될 때는 JavaMail을 사용해서 메일을 전송할 필요가 없다. 
   아무런 기능이 없는 MailSender인터페이스의 구현 빈 클래스를 생성. 
   --> DummyMailSender는 MailSender 인터페이스를 구현했을 뿐, 하는 일이 없음.
*/
public class DummyMailSender implements MailSender {

	public void send(SimpleMailMessage mailMessage) throws MailException {
	}

	public void send(SimpleMailMessage[] mailMessage) throws MailException {
	}
}
