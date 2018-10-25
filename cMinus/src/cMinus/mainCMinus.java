package cMinus;

import java.util.ArrayList;

public class mainCMinus{
	
	public static void main(String[] args) throws Exception{
		
		ArrayList<String> result = new ArrayList<String>();
		
		String address = "C:\\Users\\Dom\\Documents\\Eclipse projects\\cMinus\\src\\cMinus\\example.c";
		Lex lex = new Lex(address);
		result = lex.end();
		
		/*
		for (int i = 0; i < result.size(); i++){
			System.out.println(result.get(i));
		}
		*/
		
		semanticParser parser = new semanticParser(result);		
	}
	
}
