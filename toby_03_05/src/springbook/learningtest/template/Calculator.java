package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	public Integer calcSum(String filepath) throws IOException{
//old
//		BufferedReader br = null;
//		try{
//			br = new BufferedReader(new FileReader(filepath));
//			Integer sum = 0;
//			String line = null;
//			//마지막 라인까지 한 줄씩 읽어가면서 숫자를 더한다.
//			while((line = br.readLine()) != null){
//				sum += Integer.valueOf(line);
//			}
//			return sum;
//		}catch(IOException e){
//			System.out.println(e.getMessage());
//			throw e;
//		}finally{
//			//BufferedReader오브젝트가 생성되기 전에 예외가 발생할 수도 있으므로 반드시 null체크 먼저!  
//			if(br != null){  
//				try{ br.close(); }
//				catch(IOException e){ System.out.println(e.getMessage()); }
//			}
//		}
		
		//new -> 템플릿/콜백을 적용한 calcSum() 메소드
		BufferedReaderCallback sumCallback = new BufferedReaderCallback(){
			public Integer doSomethingWithReader(BufferedReader br) throws IOException {
				Integer sum = 0;             
				String line = null;          
				while((line = br.readLine()) != null){     
					sum += Integer.valueOf(line);          
				}                                          
				return sum;                                
			}
		};
		//템플릿이 리턴하는 값을 최종 결과로 사용한다.
		return fileReadTemplate(filepath, sumCallback);
		
	}
	
	//new -> 곱을 계산하는 콜백을 가진 calMutiply() 메소드 
	public Integer calMutiply(String filepath) throws IOException{
		BufferedReaderCallback mutiplyCallback = new BufferedReaderCallback(){
			public Integer doSomethingWithReader(BufferedReader br) throws IOException {
				Integer mutiply = 1;             
				String line = null;          
				while((line = br.readLine()) != null){     
					mutiply *= Integer.valueOf(line);          
				}                                          
				return mutiply;                                
			}
		};
		//템플릿이 리턴하는 값을 최종 결과로 사용한다.
		return fileReadTemplate(filepath, mutiplyCallback);
	}
	
	
	//new -> BufferedReaderCallback을 사용하는 템플릿 메소드
	public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException{
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(filepath));
//old 			
//			Integer sum = 0;
//			String line = null;
//			//마지막 라인까지 한 줄씩 읽어가면서 숫자를 더한다.
//			while((line = br.readLine()) != null){
//				sum += Integer.valueOf(line);
//			}
//			return sum;
			
			//new -> 콜백 오브젝트 호출. 템플릿에서 만든 컨텍스트 정보인 BufferedReader를 전달해주고 콜백의 작업 결과를 받아둔다.
			int ret = callback.doSomethingWithReader(br);
			return ret;
			
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
