package cMinus;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Lex {
	
	private static ArrayList<String> palabrasReservadas;
	private String value = "";
	private String currState = "";
	private char firstEqual = ' ';
	private int numFila = 1;
	private int numChar = 1;
	private String codigo = "";
	private int i = 0;
	private char currChar = '\0';
	private boolean goToSynt = true;
	private boolean tieneDigitoReal = false;
	private ArrayList<String> result;
		
	public Lex(String codigo) throws IOException {
		this.codigo = codigo;
		this.palabrasReservadas = new ArrayList<String>();
		this.result = new ArrayList<String>();
		palabrasReservadas.add("principal");
		palabrasReservadas.add("entero");
		palabrasReservadas.add("real");
		palabrasReservadas.add("logico");
		palabrasReservadas.add("si");
		palabrasReservadas.add("mientras");
		palabrasReservadas.add("regresa");
		palabrasReservadas.add("verdadero");
		palabrasReservadas.add("falso");
		
		FileReader fr = new FileReader(codigo);
		while((i = fr.read()) != -1){
			currChar = (char)i;
			if (currChar == '\n'){
				numFila += 1;
				numChar = -1;
			}
			
			this.check(currChar, numFila, numChar);
			numChar++;			
		}
		fr.close();
		this.end();
	}
	
	private void check(char c,  int numFila, int numChar){
		if (this.currState == "entero"){
			this.enteroState(c);	
		} else if (this.currState == "real"){
			this.realState(c);
		} else if (this.currState == "id"){
			this.idState(c);
		} else if (this.currState == "asignacion"){
			this.asignacion(c);
		} else {
			this.InitialState(c);
		}
	}

	private void InitialState(char c){
		if (Character.isDigit(c)){
			this.currState = "entero";
			this.enteroState(c);
		} else if (c == '_' || Character.isLowerCase(c)){
			this.currState = "id";
			this.idState(c);
		} else {
			if (c == '&' || c == '|' || c == '!'){
				this.result.add(String.valueOf(c));
			} else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^'){
				this.result.add(String.valueOf(c));
			} else if (c == '<' || c == '>'){
				this.result.add(String.valueOf(c));
			} else {
				switch (c){
				case ',':
					this.result.add(String.valueOf(c));
					break;
						
				case ';':
					this.result.add(String.valueOf(c));
					break;
						
				case '{':
					this.result.add(String.valueOf(c));
					break;
						
				case '}':
					this.result.add(String.valueOf(c));
					break;
						
				case '(':
					this.result.add(String.valueOf(c));
					break;
						
				case ')':
					this.result.add(String.valueOf(c));
					break;
					
				case '=':
					this.asignacion(c);
					break;
					
				case '\n':
					break;
					
				case ' ':
					break;
					
				case '\t':
					break;
					
				case '\r':
					break;
					
				default:
					this.error("Caracter invalido");
					break;
				}	
			}			
		}			
	}
	
	private void idState(char c){
		currState = "id";
		if (c == '_' || Character.isDigit(c) || Character.isLetter(c)){
			value = value + c;
		} else if (c == 'ñ' || c == 'Ñ'){
			this.error("Caracter inválido");
		} else {
			if (palabrasReservadas.contains(value)){
				this.result.add(value);
			} else {
				this.result.add(value);
			}
			currState = "";
			value = "";
			this.check(c, this.numFila, ++this.numChar);
		}
	}
	
	private void enteroState(char c){
		currState = "entero";
		if (Character.isDigit(c)){
			value = value + c;
		} else if (c == '.'){
			value = value + c;
			this.tieneDigitoReal = false;
			currState = "real";
		} else if (Character.isLetter(c)){
			this.error("Entero inválido");
		} else if (c == '_') {
			this.error("Entero inválido");
		} else {
			this.result.add(value);
			currState = "";
			value = "";
			this.check(c, this.numFila, ++this.numChar);
		}
	}
	
	private void realState(char c){
		if (Character.isDigit(c)){
			this.tieneDigitoReal = true;
			value = value + c;
		} else if ((c == ' ' || c == ';') && !this.tieneDigitoReal){
			this.error("Real inválido");
		} else {
			this.result.add(value);
			currState = "";
			value = "";
			this.check(c, this.numFila, ++this.numChar);
		}
	}
	
	private void asignacion(char c){
		currState = "asignacion";
		if (firstEqual == '=' && c == '='){
			this.relacional(c);
		} else {
			if (firstEqual == ' '){
				firstEqual = c;
			} else if (firstEqual == '=' || c != '=') {
				this.result.add(firstEqual+"");
				currState = "";
				firstEqual = ' ';
				this.check(c, this.numFila, ++this.numChar);
			}
		}
	}
	
	private void relacional(char c){
		if (firstEqual != ' '){
			String equals = firstEqual+"";
		}
		this.result.add("" + firstEqual + c);
		firstEqual = ' ';
		currState = "";
	}
	
	private void error(String msg){
		this.goToSynt = false;
	}
	
	public boolean goToSynt(){
		return this.goToSynt;
	}
	
	public ArrayList<String> end(){
		return result;
	}
}