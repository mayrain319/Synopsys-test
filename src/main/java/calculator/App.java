package calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * synopsys test
 *
 */
public class App 
{
	private static String EXCEPTION_INVALID_MESSAGE = "Not a valid expression";
	private static String EXCEPTION_OVERFLOW_MESSAGE = "The target value is overflow";
	private static HashMap<String, Integer> hMap = new HashMap<String, Integer>();
	
	private static Logger logger = Logger.getLogger(App.class);
	
	public static void main(String[] args) {
		
		BasicConfigurator.configure();
		
		String formula = "";
		String logLevel = "";
		
		if (args.length > 0){
		    formula = args[0].replaceAll(" ", "").toLowerCase();
		    if (args.length == 2){
		    	logLevel = args[1];
		    } 
		}
		
		if (logLevel.length() > 0){
			if (logLevel.equalsIgnoreCase("-d")){
				logger.setLevel(Level.DEBUG);
			}
			else if (logLevel.equalsIgnoreCase("-i")){
				logger.setLevel(Level.INFO);
			}
			else if (logLevel.equalsIgnoreCase("-e")){
				logger.setLevel(Level.ERROR);
			}
		}
		
		logger.info("formula is " + formula);
		
		if (!isExpression(formula)){
			logger.error(EXCEPTION_INVALID_MESSAGE, null);
			return;
		}
			
		try {
			int val = calculateInput(formula);
			logger.info("result is " + val);
			System.out.println(val);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		System.out.println();
		
	}
	
	public static int calculateInput(String input) throws Exception{
		if (isInteger(input)){
			logger.debug("parameter : " + input);
			return Integer.valueOf(input);
		}
		
		if (!isExpression(input)){
			logger.debug("get parameter value from hashmap, " + input + "=" + hMap.get(input));
			return hMap.get(input);
		}
		
		logger.debug("calculate formula " + input );
		String abstractedParam = abstractParameters(input);
		logger.debug("abstracted parameters: " + abstractedParam);
		String[] params = processParameters(abstractedParam);
		
		int returnVal = 0;
		if (input.startsWith("add")){
			if (params.length != 2)
				throw new Exception(EXCEPTION_INVALID_MESSAGE);
			
			int value1 = calculateInput(params[0]);
			int value2 = calculateInput(params[1]);
			
			if (value1 > 0 && value2 > 0 && value1 > Integer.MAX_VALUE - value2){
				throw new Exception(EXCEPTION_OVERFLOW_MESSAGE);
			}			
			if (value1 < 0 && value2 < 0 && value1 < Integer.MIN_VALUE - value2){
				throw new Exception(EXCEPTION_OVERFLOW_MESSAGE);
			}
			returnVal = value1 + value2;
			
		}
		else if (input.startsWith("sub")){
			if (params.length != 2)
				throw new Exception(EXCEPTION_INVALID_MESSAGE);
			
			int value1 = calculateInput(params[0]);
			int value2 = calculateInput(params[1]);
			
			if (value1 < 0 && value2 > 0 && value1 < Integer.MIN_VALUE + value2){
				throw new Exception(EXCEPTION_OVERFLOW_MESSAGE);
			}			
			if (value1 > 0 && value2 < 0 && value1 > Integer.MAX_VALUE + value2){
				throw new Exception(EXCEPTION_OVERFLOW_MESSAGE);
			}
			
			returnVal = value1 - value2;
			
		}
		else if (input.startsWith("mult")){
			if (params.length != 2)
				throw new Exception(EXCEPTION_INVALID_MESSAGE);
			
			int value1 = calculateInput(params[0]);
			int value2 = calculateInput(params[1]);
			
			if (isSameSign(value1, value2)){
				if (Math.abs(value2) > 1 && Math.abs(value1) > Integer.MAX_VALUE / Math.abs(value2)){
					throw new Exception(EXCEPTION_OVERFLOW_MESSAGE);
				}
			}
			else{
				if (Math.abs(value2) < -1 && Math.abs(value1) > Math.abs(Integer.MIN_VALUE/value2)){
					throw new Exception(EXCEPTION_OVERFLOW_MESSAGE);
				}
			}
			
			returnVal = value1*value2;
			
		}
		else if (input.startsWith("div")){
			if (params.length != 2)
				throw new Exception(EXCEPTION_INVALID_MESSAGE);
			
			int value1 = calculateInput(params[0]);
			int value2 = calculateInput(params[1]);
			
			if (value2 == 0){
				throw new Exception("0 could be used as devisor.");
			}
			
			returnVal = value1/value2;
			
		}
		else if (input.startsWith("let")){
			if (params.length < 3)
				throw new Exception(EXCEPTION_INVALID_MESSAGE);
			
			String value1 = params[0];
			String value2 = params[1];
			
			if (hMap.containsKey(value1)){
				hMap.remove(value1);
			}
			logger.debug("set " + value1 + " to " + value2);
			hMap.put(value1, calculateInput(value2));
			
			for (int i = 2; i < params.length; i++){
				returnVal = calculateInput(params[i]);
			}
		}
		
		logger.debug("result of " + input + " is " + returnVal);
		return returnVal;
	}
	
	private static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
	
	private static boolean isExpression(String s){
		String regexFront = "(?<=^((add)|(sub)|(mult)|(div)|(let))\\()";
		String regexMid = "((.+)+,.+)";
		String regexEnd = "(?=\\)$)";
		Pattern pattern = Pattern.compile(regexFront + regexMid + regexEnd);
		Matcher matcher = pattern.matcher(s);
		
		return matcher.find();		
	}
	
	private static boolean isSameSign(int a, int b){
		return ((a >> 31) == (b >> 31));
	}	
	
	private static String abstractParameters(String input){
		
		logger.debug("abstractParameters: " + input);
		
		String regexFront = "(?<=^((add)|(sub)|(mult)|(div)|(let))\\()";
		String regexMid = "((.+)+,.+)";
		String regexEnd = "(?=\\)$)";
		Pattern pattern = Pattern.compile(regexFront + regexMid + regexEnd);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()){			
			return matcher.group();
		}
		
		return "";
	}
	
	private static String[] processParameters(String input){
		ArrayList<String> params = new ArrayList<String>();
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		int cnt = 0;
		for (int i = 0; i < input.length(); i++){
			if (input.charAt(i) == '('){
				cnt++;
			}
			if (input.charAt(i) == ')'){
				cnt--;
			}
			if (input.charAt(i) == ',' && cnt == 0){
				indexes.add(i);
			}
		}
		
		int curr = 0;
		for (int i = 0; i < indexes.size();i++){
			params.add(input.substring(curr, indexes.get(i)));
			curr = indexes.get(i) + 1;
		}
		params.add(input.substring(curr));
		
		return params.toArray(new String[params.size()]);
	}

}
