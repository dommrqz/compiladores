package cMinus;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Stack;

public class semanticParser{
	
	private ArrayList<String> code;	
	private ArrayList<String> functions;
	private ArrayList<Integer> numVariables;
	private PrintWriter pw;
	private int tempVariable = 1;
	private int label = 1;
	private int codeCount = 0;
	private String tempVariableString = "";
	private String tempLabel = "";
	private Stack<String> labels = new Stack<String>();
	private boolean inIf;
	private boolean inWhile;
	private boolean oneSentenceIf;
	String varLab = "";
	String varTemp = "";
	
	public semanticParser(ArrayList<String> code) throws IOException{
		this.code = code;
		this.inIf = false;
		this.inWhile = false;
		this.oneSentenceIf = false;
		functions = new ArrayList<String>();
		numVariables = new ArrayList<Integer>();
		pw = new PrintWriter(new FileWriter("result.txt"));
		
		this.checkString(this.code.get(this.codeCount));
	}
	
	private void checkString(String s){
		try{
			switch(s){	
				case "int":
					this.checkString(this.code.get(++this.codeCount));
					break;
				
				case "void":
					this.checkString(this.code.get(++this.codeCount));
					break;
			
				case "(":
					checkParentesis();
					break;
					
				case ")":
					this.checkString(this.code.get(++this.codeCount));
					break;
					
				case "{":
					this.checkString(this.code.get(++this.codeCount));
					break;
					
				case "}":
					if (this.inIf){
						this.ifLoop(false);
					} else if (this.inWhile) {
						this.whileLoop(false);
					}else {
						this.checkString(this.code.get(++this.codeCount));
					}
					break;
					
					
				case "if":
					this.checkString(this.code.get(++this.codeCount));
					break;
					
				case "while":
					this.checkString(this.code.get(++this.codeCount));
					break;
					
				case ";":
					if (this.oneSentenceIf){
						this.pw.println("Label " + this.labels.pop());
					}
					this.oneSentenceIf = false;
					this.checkString(this.code.get(++this.codeCount));
					break;
					
				case "return":
					this.checkReturn();
					break;
					
				case "else":
					this.checkElse();
					break;
					
				default:
					checkVar(s);
					break;
			}
		} catch (IndexOutOfBoundsException e){
			this.pw.println("return");
			this.pw.close();
			return;
		}
	}
	
	private void checkVar(String s){
		if (this.code.get(this.codeCount + 1).equals("(") && !this.functions.contains(s) && 
				!this.code.get(this.codeCount).equals("write")) {
			this.pw.print("entry " + s + "\n");
			this.functions.add(s);
		} else if (this.code.get(this.codeCount + 2).equals("read")){
			this.goToRead();
		} else if (this.code.get(this.codeCount).equals("write")){
			this.goToWrite();
		}
		
		this.checkString(this.code.get(++this.codeCount));
	}
	
	private void checkParentesis(){
		if (this.code.get(this.codeCount - 1).equals("if")){
			this.goToIf();
		} else if (this.code.get(this.codeCount - 1).equals("while")){
			this.goToWhile();
		} else {
			this.countParams();
		}
	}
	
	private void goToIf(){
		String resTemp = "";
		this.codeCount++;
		String sTemp = this.code.get(this.codeCount);
		while(!sTemp.equals(")")){
			resTemp = resTemp + " " + sTemp;
			sTemp = this.code.get(++this.codeCount);
		}

		this.tempVariableString = this.generateTempVariable();
		this.tempLabel = this.generateLabel();
		this.pw.println(this.tempVariableString + " = " + resTemp);
		this.pw.println("if false " + this.tempVariableString + " goto " + tempLabel);
		this.labels.push(tempLabel);
		
		if (this.code.get(this.codeCount + 1).equals("{")){
			this.inIf = true;
			this.ifLoop(true);
		} else {
			this.oneSentenceIf = true;
			this.checkString(this.code.get(++this.codeCount));
		}		
	}
	
	private void ifLoop(boolean value){
		while(value){
			this.checkString(this.code.get(++this.codeCount));
		}
		this.inIf = false;
		this.pw.println("Label " + this.labels.pop());
		this.checkString(this.code.get(++this.codeCount));
	}
	
	private void goToWhile(){
		String resTemp = "";
		
		this.codeCount++;
		String sTemp = this.code.get(this.codeCount);
		while(!sTemp.equals(")")){
			resTemp = resTemp + " " + sTemp;
			sTemp = this.code.get(++this.codeCount);
		}
		
		varTemp = this.generateTempVariable();
		varLab = this.generateLabel();
		this.labels.push(varLab);
		this.pw.println("Label " + varLab);
		varTemp = this.generateLabel();
		this.pw.println("if false" + resTemp + " goto " + varTemp);
		this.whileLoop(true);
	}
	
	private void whileLoop(boolean value){
		this.inWhile = true;
		while(!this.code.get(this.codeCount).equals("}")){
			this.checkString(this.code.get(++this.codeCount));
		}
		this.inWhile = false;
		this.pw.println("goto " + this.labels.pop());
		this.pw.println("Label " + varTemp);
		this.checkString(this.code.get(++this.codeCount));
	}
	
	private void countParams(){
		String tempString = "";
		int tempCount = 0;
		
		while(!this.code.get(++this.codeCount).equals(")")){
			tempString = this.code.get(this.codeCount);
			if (!tempString.equals("int") && !tempString.equals(",")){
				tempCount++;
			}
		}
		this.numVariables.add(tempCount);
		this.checkString(this.code.get(++this.codeCount));
	}
	
	private void checkReturn(){
		String stringReturn = "";
		String tempVariable = "";
		if (this.code.get(this.codeCount + 2).equals(";")){
			this.pw.println("return " + this.code.get(this.codeCount + 1));
		} else { 
			while(!this.code.get(++this.codeCount).equals(";")){
				if (this.code.get(this.codeCount + 1).equals("(")){
					stringReturn = stringReturn + " " + this.callFunction();
				} else {
					stringReturn = stringReturn + " " + this.code.get(this.codeCount);
				}
			}
			tempVariable = this.generateTempVariable();
			this.pw.println(tempVariable + " =" + stringReturn);
			this.pw.println("return " + tempVariable);
		}
		this.oneSentenceIf = false;
		this.checkString(this.code.get(++this.codeCount));
	}
	
	private String callFunction(){
		String funcName = this.code.get(this.codeCount);
		String tempVarString = "";
		String tempVarName = "";
		
		this.pw.println("begin_params");
		
		this.codeCount++;
		for (int i = 0; i < this.numVariables.get(this.functions.indexOf(funcName)); i++){
			this.codeCount++;
			while(!this.code.get(this.codeCount).equals(")") && 
					!this.code.get(this.codeCount).equals(",")){
				tempVarString = tempVarString + this.code.get(this.codeCount);
				this.codeCount++;
			}
			tempVarName = this.generateTempVariable();
			this.pw.println(tempVarName + " = " + tempVarString);
			this.pw.println("param " + tempVarName);
			tempVarString = "";
		}
		tempVarName = this.generateTempVariable();
		this.pw.println(tempVarName + " = call " + funcName + ", " + this.numVariables.get(this.functions.indexOf(funcName)));
		return tempVarName;
	}
	
	private void checkElse(){
		if(!this.code.get(this.codeCount - 1).equals("}") && !this.oneSentenceIf){
			this.pw.println("Label " + this.labels.pop());
		}
		
		this.checkString(this.code.get(++this.codeCount));
		
	}
	
	private void goToRead(){
		this.pw.println("read " + this.code.get(this.codeCount));
		this.codeCount += 5;
		this.checkString(this.code.get(++this.codeCount));
	}
	
	private void goToWrite(){
		this.codeCount += 2;
		if (this.functions.contains(this.code.get(this.codeCount))){
			this.pw.println("write " + this.callFunction());
		} else {
			this.pw.println("write " + this.code.get(this.codeCount));
		}
		this.checkString(this.code.get(++this.codeCount));
	}
	
	private String generateTempVariable(){
		return "t" + tempVariable++;
	}
	
	private String generateLabel(){
		return "L" + label++;
	}
}