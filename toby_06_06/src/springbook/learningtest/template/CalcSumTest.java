package springbook.learningtest.template;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class CalcSumTest {
	Calculator calculator;
	String numFilepath;
	
	//각 테스트 메소드에서 사용할 클래스의 오브젝트와 파일 이름이 공유됨.
	//->그렇다면 @Before메소드에서 미리 픽스처로 만들어두는게 좋다.
	@Before
	public void SetUp(){
		this.calculator = new Calculator();
		this.numFilepath = getClass().getResource("numbers.txt").getPath();
	}
	
	@Test
	public void sumOfNumbers() throws IOException {
		assertThat(calculator.calcSum(this.numFilepath), is(10));
	}
	
	@Test
	public void multiplyOfNumbers() throws IOException {
		assertThat(this.calculator.calMutiply(this.numFilepath), is(24));
	}
	
	@Test
	public void concatenateStrings() throws IOException {
		assertThat(this.calculator.concatenate(this.numFilepath), is("1234"));
	}
	
}
