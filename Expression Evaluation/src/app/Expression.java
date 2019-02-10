package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	String expression="";
    	ArrayList<String> checked=new ArrayList<String>();
    	for(int i=0;i<expression.length();i++){
    		expression+=expr.charAt(i);
    		if(expr.charAt(i)=='['){
    			expression+='$';
    		}
    	}
    	StringTokenizer s=new StringTokenizer(expression, "t*+-/()]$0123456789");
    	String[] token=new String[s.countTokens()];
    	for(int i=0;i<token.length;i++){
    		token[i]=s.nextToken();
    	}
    	for(int i=0;i<token.length;i++){
    		String curr=token[i];
    		if(curr.contains("[")){
    			if(checked.contains(curr)){
    				continue;
    			}else{
    				arrays.add(new Array(curr.substring(0,curr.length()-1)));
    				checked.add(curr);
    			}
    		}else if(checked.contains(curr)){
    			continue;
    		}else{
    			vars.add(new Variable(curr));
    			checked.add(curr);
    		}
    	}
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */ 
    private static boolean numeric(String s) {
        if(Pattern.matches("[0-9.E]+", s) && Pattern.matches("[a-zA-Z]", s) == false && Pattern.matches("/[|/]", s) == false ) {
            return true;  
        }return false;
    }

    private static boolean basic(String s) {
        if(Pattern.matches("[a-zA-Z]+", s) && Pattern.matches("[0-9]+", s) == false && Pattern.matches("/[|/]", s) == false ) {
            return true;
        }return false;
    }

    private static int numberOp(String a) {
         int number = 0;
         for(int i = 0; i < a.length(); i++) {
             if(a.charAt(i) == '+' || a.charAt(i) == '-' 
            		 || a.charAt(i) == '*' || a.charAt(i) == '/') {
                 number++;
             }
         }
         return number;
    }

    private static boolean symbol(char a) {
        if(a == '+' || a == '-' || a == '*' 
        		|| a == '/' || a == '(' || a == ')' || a == '[' || a == ']') {
            return true;
        }
        return false;
    }

    private static boolean operator(String a) {
        if(a.equals("+") || a.equals("-") || a.equals("*") || a.equals("/")) {
            return true;
        }
        else return false;

    }

    private static boolean higher(String a, String b) {
        if(a.equals("*") && b.equals("+")) {
            return true;
        }
        else if(a.equals("*") && b.equals("-")) {
            return true;
        }
        else if(a.equals("/") && b.equals("+")) {
            return true;
        }
        else if(a.equals("/") && b.equals("-")) {
            return true;
        }
        else {
            return false;
        }
    }
    private static String prep(String a) {
        a = a.replaceAll(" ", "");
        String newString = "";
        for(int i = 0; i < a.length(); i++) {
            if(a.substring(i, i+1).matches("[a-zA-Z]")) {
                newString += a.charAt(i);
            }
            else if(a.substring(i, i+1).matches("[0-9]")) {
                newString += a.charAt(i);
            }
            else if(symbol(a.charAt(i))) {
                newString += '$';
                newString += a.charAt(i);
                newString += '$';
            }
        }
        char[] chars = newString.toCharArray();
        String finalString = "";
        for(int i = 1; i < chars.length; i++) {
            if(chars[i] == chars[i-1] && chars[i] == '$') {
                continue;
            }

            if(i == chars.length - 1) {
                finalString += chars[i-1];
                finalString += chars[i];
            }else
                finalString += chars[i-1];  
        }
        return finalString;
    }
    
    private static void stacks(Stack<String> a, Stack<String> b, 
    		ArrayList<Variable> c, ArrayList<Array> d) {
        if(a.isEmpty() == false && b.isEmpty() == false) {
            String term = a.pop();
            String operator = b.pop();
            String term2 = a.pop();
            String combined = term2 + operator + term;
            float result = evaluate(combined, c, d);
            a.push(Float.toString(result));
        }
    }
    
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
        String exp = expr.replaceAll(" ", "");
        Stack<String> operands = new Stack<String>();
        Stack<String> operators = new Stack<String>();
        if(numeric(exp)) {
                return Float.parseFloat(exp);

        }
        else if(basic(exp)) {
            int index = 0;
            for(int i = 0; i < vars.size(); i++) {
                if(vars.get(i).name.equals(exp)) {
                    index = i;
                }
            }
            return (float) (vars.get(index).value);
        }
        else if(numberOp(exp) == 1) {
            int indexOp = 0;
            for(int i = 0; i < exp.length(); i++) {
                if( operator(exp.substring(i, i+1))) {
                    indexOp = i;
                }
            }
            if(exp.charAt(indexOp) == '+') {
                return (float) evaluate(exp.substring(0, indexOp), vars, arrays) + evaluate(exp.substring(indexOp + 1), vars, arrays);
            }
            else if(exp.charAt(indexOp) == '-') {
                return (float) evaluate(exp.substring(0, indexOp), vars, arrays) - evaluate(exp.substring(indexOp + 1), vars, arrays);
            }
            else if(exp.charAt(indexOp) == '*') {
                return (float) evaluate(exp.substring(0, indexOp), vars, arrays) * evaluate(exp.substring(indexOp + 1), vars, arrays);
            }
            else {
                return (float) evaluate(exp.substring(0, indexOp), vars, arrays) / evaluate(exp.substring(indexOp + 1), vars, arrays);
            }
        }  
        else if(numberOp(exp) > 1) {
            String modded = prep(exp);
            StringTokenizer st = new StringTokenizer(modded, "$");
            String[] tokens = new String[st.countTokens()];
            for(int i = 0; i < tokens.length; i++) {
                tokens[i] = st.nextToken();
            }

            int j = 0;
            while(j < tokens.length) {
                if(numeric(tokens[j]) || basic(tokens[j])) {
                    operands.push(tokens[j]);
                }
                else if(operator(tokens[j]) && operators.isEmpty()) {
                    operators.push(tokens[j]);
                }
                else if(operator(tokens[j]) && operators.isEmpty() == false) {
                    String topOfOperatorsStack = operators.peek();
                    if(higher(tokens[j], topOfOperatorsStack)) {
                        operators.push(tokens[j]);
                    }
                    else {
                    	stacks(operands, operators, vars, arrays);
                        j--;
                    }
                }
                j++;
            }
            while(operators.isEmpty() == false) {
            	stacks(operands, operators, vars, arrays);
            }
            return Float.parseFloat(operands.pop());
        }
        return 0f;
}
}
