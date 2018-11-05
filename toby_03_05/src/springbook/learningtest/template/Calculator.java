package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	public Integer calcSum(String filepath) throws IOException{
		//한 줄씩 읽기 편하게 BufferedReader로 파일을 가져온다.
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(filepath));
			Integer sum = 0;
			String line = null;
			//마지막 라인까지 한 줄씩 읽어가면서 숫자를 더한다.
			while((line = br.readLine()) != null){
				sum += Integer.valueOf(line);
			}
			return sum;
		}catch(IOException e){
			System.out.println(e.getMessage());
			throw e;
		}finally{
			//BufferedReader오브젝트가 생성되기 전에 예외가 발생할 수도 있으므로 반드시 null체크 먼저!  
			if(br != null){  
				try{ br.close(); }
				catch(IOException e){ System.out.println(e.getMessage()); }
			}
		}
	}
}
