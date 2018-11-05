package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	public Integer calcSum(String filepath) throws IOException{
//old		
//		// 템플릿/콜백을 적용한 calcSum() 메소드
//		BufferedReaderCallback sumCallback = new BufferedReaderCallback(){
//			public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//				Integer sum = 0;             
//				String line = null;          
//				while((line = br.readLine()) != null){     
//					sum += Integer.valueOf(line);          
//				}                                          
//				return sum;                                
//			}
//		};
//		//템플릿이 리턴하는 값을 최종 결과로 사용한다.
//		return fileReadTemplate(filepath, sumCallback);
		
		//new 
		LineCallback sumCallback = new LineCallback(){
			public Integer doSomethingWithLine(String line, Integer value) {
				//value += Integer.valueOf(line);
				//return value;
				return value + Integer.valueOf(line); //루프를 돌면서 라인 한줄을 읽을때마다 수행하는 동작.
			}
		};
		//템플릿이 리턴하는 값을 최종 결과로 사용한다.
		return lineReadTemplate(filepath, sumCallback, 0);
	}
	
	
	// 곱을 계산하는 콜백을 가진 calMutiply() 메소드 
	public Integer calMutiply(String filepath) throws IOException{
		
//old		
//		BufferedReaderCallback mutiplyCallback = new BufferedReaderCallback(){
//			public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//				Integer mutiply = 1;             
//				String line = null;          
//				while((line = br.readLine()) != null){     
//					mutiply *= Integer.valueOf(line);          
//				}                                          
//				return mutiply;                                
//			}
//		};
//		//템플릿이 리턴하는 값을 최종 결과로 사용한다.
//		return fileReadTemplate(filepath, mutiplyCallback);
		
		//new
		LineCallback mutiplyCallback = new LineCallback(){
			public Integer doSomethingWithLine(String line, Integer value) {
				return value * Integer.valueOf(line); 
			}
		};
		return lineReadTemplate(filepath, mutiplyCallback, 1);
	}
	
//old	
//	// BufferedReaderCallback을 사용하는 fileRead 템플릿 메소드
//	public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException{
//		BufferedReader br = null;
//		try{
//			br = new BufferedReader(new FileReader(filepath));
//			//콜백 오브젝트 호출. 템플릿에서 만든 컨텍스트 정보인 BufferedReader를 전달해주고 콜백의 작업 결과를 받아둔다.
//			int ret = callback.doSomethingWithReader(br);
//			return ret;
//			
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
//	}
	
	
	//new -> LineCallback을 사용하는 템플릿 메소드
	public Integer lineReadTemplate(String filepath, LineCallback callback, int initVal/*계산 결과를 저장할 변수의 초기값*/) 
			throws IOException{
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(filepath));
			
//			Integer mutiply = 1;             
//			String line = null;          
//			while((line = br.readLine()) != null){     
//				mutiply *= Integer.valueOf(line);          
//			}                                          
//			return mutiply;        
			
			//new 
			int res = initVal;
			String line = null; 
			while((line = br.readLine()) != null){ //파일의 각 라인을 루프를 돌면서 가져오는 것도 템플릿이 담당.
				res = callback.doSomethingWithLine(line, res);
				//res -> 콜백이 계산한 값을 저장해뒀다가 다음 라인 계산에 다시 사용한다.
				//각 라인의 내용을 가지고 계산하는 작업만 콜백에게 맡긴다.
			}
			return res;
			
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
