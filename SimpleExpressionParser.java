package application;

public class SimpleExpressionParser implements ExpressionParser {
        /*
         * Attempts to create an expression tree from the specified String.
         * Throws a ExpressionParseException if the specified string cannot be parsed.
	 * Grammar:
	 * S -> A | P
	 * A -> A+M | A-M | M
	 * M -> M*E | M/E | E
	 * E -> P^E | P | log(P)
	 * P -> (S) | L | V
	 * L -> <float>
	 * V -> x
         * @param str the string to parse into an expression tree
         * @return the Expression object representing the parsed expression tree
         */
	public Expression parse (String str) throws ExpressionParseException {
		str = str.replaceAll(" ", "");
		Expression expression = parseAdditiveExpression(str);
		if (expression == null) {
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		return expression;
	}
	
	protected Expression parseAdditiveExpression (String str) {
		int count = 0;
	    for (int i = 0; i < str.length(); i++) {
	    	if (str.substring(i, i + 1).equals("(")) {
	    		count++;
	    	}
	    	if (str.substring(i, i + 1).equals(")")) {
	    		count--;
	    	}
	        if (str.substring(i, i + 1).equals("+") && count == 0) {
	        	ParsedExpression p1 = new ParsedExpression(str.substring(i, i + 1));
	            Expression left = parseMultiplicativeExpression(str.substring(0, i));
	            p1.addSubexpression(left);
	            if (str.length()-i > 1) {
					Expression right = parseAdditiveExpression(str.substring(i + 1, str.length()));
					p1.addSubexpression(right);
				}
	            return p1;
	        }
	        if (str.substring(i, i + 1).equals("-") && i!=0 && count == 0) {
	        	if (!str.substring(i-1, i).equals("*") && !str.substring(i-1, i).equals("+")
	        		&& !str.substring(i-1, i).equals("/") && !str.substring(i-1, i).equals("^") 
	        		&& !str.substring(i-1, i).equals("-")) {
	        		ParsedExpression p1 = new ParsedExpression(str.substring(i, i + 1));
	            Expression left = parseMultiplicativeExpression(str.substring(0, i));
	            p1.addSubexpression(left);
	            if (str.length()-i > 1) {
					Expression right = parseAdditiveExpression(str.substring(i + 1, str.length()));
					p1.addSubexpression(right);
				}
	            return p1;
	        	}
	        }
	    }
	    return parseMultiplicativeExpression(str);
	}
	
	protected Expression parseMultiplicativeExpression (String str) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.substring(i, i + 1).equals("(")) {
	    		count++;
	    	}
	    	if (str.substring(i, i + 1).equals(")")) {
	    		count--;
	    	}
			if (str.substring(i, i+1).equals("*") && count == 0) {
				ParsedExpression p1 = new ParsedExpression (str.substring(i, i+1));
				Expression left = parseExponentialExpression(str.substring(0, i));
				p1.addSubexpression(left);
				if (str.length()-i > 1) {
					Expression right = parseAdditiveExpression(str.substring(i + 1, str.length()));
					p1.addSubexpression(right);
				}
				return p1;
			}
			if (str.substring(i, i+1).equals("/") && count == 0) {
				ParsedExpression p1 = new ParsedExpression (str.substring(i, i+1));
				Expression left = parseExponentialExpression(str.substring(0, i));
				p1.addSubexpression(left);
				if (str.length()-i > 1) {
					Expression right = parseAdditiveExpression(str.substring(i + 1, str.length()));
					p1.addSubexpression(right);
				}
				return p1;
			}
		}
		return parseExponentialExpression(str);
	}
	
	protected Expression parseExponentialExpression (String str) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.substring(i, i + 1).equals("(")) {
	    		count++;
	    	}
	    	if (str.substring(i, i + 1).equals(")")) {
	    		count--;
	    	}
			if (str.substring(i, i+1).equals("^") && count == 0) {
				ParsedExpression p1 = new ParsedExpression (str.substring(i, i+1));
				Expression left = parseParenthesesExpression(str.substring(0, i));
				p1.addSubexpression(left);
				if (str.length()-i > 1) {
					Expression right = parseAdditiveExpression(str.substring(i + 1, str.length()));
					p1.addSubexpression(right);
				}
				return p1;
			}
			if (str.length()-i >= 3 && str.substring(i, i+3).equals("log")) {
				for (int j = i+3; j < str.length(); j++) {
					if (str.substring(j,j+1).equals(")")) {
						ParsedExpression p1 = new ParsedExpression ("log");
						Expression child = parseAdditiveExpression(str.substring(i+4,j));
						p1.addSubexpression(child);
						return p1;
					}
				}
			}
		}
		return parseParenthesesExpression(str);
	}
	
	protected Expression parseParenthesesExpression (String str) {
		if (str.substring(0,1).equals("(")) {
			if (str.substring(str.length()-1,str.length()).equals(")")) {
				if (str.length()>2) {
				ParsedExpression parent = new ParsedExpression (str.substring(0,1) + str.substring(str.length()-1,str.length()));
				Expression insideParent = parseAdditiveExpression(str.substring(1, str.length()-1));
				parent.addSubexpression(insideParent);
				return parent;
				}
				else {
					ParsedExpression sadParent = new ParsedExpression (str.substring(0,1) + str.substring(str.length()-1,str.length()));
					return sadParent;
				}
			}
		}
		if (ParsedExpression.isNumber(str)) {
			return parseLiteralExpression(str);
		}
		if (str.equals("x")) {
			return parseVariableExpression(str);
		}
		return new ParsedExpression(str);
	}

	// TODO: once you implement a VariableExpression class, fix the return-type below.
	protected ParsedExpression parseVariableExpression (String str) {
		if (str.equals("x")) {
			return new ParsedExpression(str);
		}
		else return null;
	}

        // TODO: once you implement a LiteralExpression class, fix the return-type below.
	protected ParsedExpression parseLiteralExpression (String str) {
		// From https://stackoverflow.com/questions/3543729/how-to-check-that-a-string-is-parseable-to-a-double/22936891:
		final String Digits     = "(\\p{Digit}+)";
		final String HexDigits  = "(\\p{XDigit}+)";
		// an exponent is 'e' or 'E' followed by an optionally 
		// signed decimal integer.
		final String Exp        = "[eE][+-]?"+Digits;
		final String fpRegex    =
		    ("[\\x00-\\x20]*"+ // Optional leading "whitespace"
		    "[+-]?(" +         // Optional sign character
		    "NaN|" +           // "NaN" string
		    "Infinity|" +      // "Infinity" string

		    // A decimal floating-point string representing a finite positive
		    // number without a leading sign has at most five basic pieces:
		    // Digits . Digits ExponentPart FloatTypeSuffix
		    // 
		    // Since this method allows integer-only strings as input
		    // in addition to strings of floating-point literals, the
		    // two sub-patterns below are simplifications of the grammar
		    // productions from the Java Language Specification, 2nd 
		    // edition, section 3.10.2.

		    // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
		    "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

		    // . Digits ExponentPart_opt FloatTypeSuffix_opt
		    "(\\.("+Digits+")("+Exp+")?)|"+

		    // Hexadecimal strings
		    "((" +
		    // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
		    "(0[xX]" + HexDigits + "(\\.)?)|" +

		    // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
		    "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

		    ")[pP][+-]?" + Digits + "))" +
		    "[fFdD]?))" +
		    "[\\x00-\\x20]*");// Optional trailing "whitespace"

		if (str.matches(fpRegex)) {
			double placehold = Double.parseDouble(str);
			str = Double.toString(placehold);
			return new ParsedExpression(str);
		}
		return null;
	}

	public static void main (String[] args) throws ExpressionParseException {
		final ExpressionParser parser = new SimpleExpressionParser();
		System.out.println(parser.parse("10*2+12-4.").convertToString(0));
	}
}
