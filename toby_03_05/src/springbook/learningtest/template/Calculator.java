package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	public Integer calcSum(String filepath) throws IOException{
		//한 줄씩 읽기 편하게 BufferedReader로 파일을 가져온다.
		BufferedReader br = new BufferedReader(new FileReader(filepath));
		Integer sum = 0;
		String line = null;
		//마지막 라인까지 한 줄씩 읽어가면서 숫자를 더한다.
		while((line = br.readLine()) != null){
			sum += Integer.valueOf(line);
		}
		br.close(); //한 번 연 파일은 반드시 닫아줌.
		return sum;
	}
}
