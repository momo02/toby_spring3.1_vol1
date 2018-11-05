package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	public Integer calcSum(String filepath) throws IOException{
		LineCallback<Integer> sumCallback = new LineCallback<Integer>(){
			public Integer doSomethingWithLine(String line, Integer value) {
				return value + Integer.valueOf(line); //루프를 돌면서 라인 한줄을 읽을때마다 수행하는 동작.
			}
		};
		//템플릿이 리턴하는 값을 최종 결과로 사용한다.
		return lineReadTemplate(filepath, sumCallback, 0);
	}
	
	
	// 곱을 계산하는 콜백을 가진 calMutiply() 메소드 
	public Integer calMutiply(String filepath) throws IOException{
		LineCallback<Integer> mutiplyCallback = new LineCallback<Integer>(){
			public Integer doSomethingWithLine(String line, Integer value) {
				return value * Integer.valueOf(line); 
			}
		};
		return lineReadTemplate(filepath, mutiplyCallback, 1);
	}

	
	public String concatenate(String filepath) throws IOException{
		//★콜백을 정의할 때 사용할 타입을 지정한다
		LineCallback<String> concatenateCallback = new LineCallback<String>(){
			public String doSomethingWithLine(String line, String value) {
				return value + Integer.valueOf(line); 
			}
		};
		return lineReadTemplate(filepath, concatenateCallback, ""); //템플릿 메소드의 T는 모두 String이 된다.
		//최종적으로 lineReadTemplate()메소드의 결과도 String 타입이 되어 concatenate메소드의 리턴 타입도 String으로 정의.
	}
	
	
	//new -> 타입 파라미터를 추가해서 제네릭 메소드로 만든 lineReadTemplate()
	/* cf. 제네릭 메소드 : 매개타입과 리턴 타입으로 타입 파라미터를 갖는 메소드
	 *  제네릭 메소드를 선언하는 방법 : 리턴 타입 앞에 <> 기호를 추가하고 타입 파라미터를 기술한 다음, 리턴 타입과 매개 타입으로 타입 파라미터를 사용.
	 *  ex) public <타입 파라미터, ...> 리턴타입 메소드명(매개변수, ...) { ... }
	 */
	public <T> T lineReadTemplate(String filepath, LineCallback<T> callback, T initVal/*계산 결과를 저장할 변수의 초기값*/) 
			throws IOException{
		BufferedReader br = null;
		try{
			br = new BufferedReader(new FileReader(filepath));
			T res = initVal;
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
