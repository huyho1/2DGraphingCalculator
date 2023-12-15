package application;

import java.util.ArrayList;
import java.util.List;


public class ParsedExpression implements Expression{
	private List<Expression> _children;
	private Expression _parent;
	private String _name;
	
	public ParsedExpression(String name) {
		_parent = null;
		_children = new ArrayList<Expression>();
		_name = name;
	}
	
	/**
	 * Sets a subexpression to its parent
	 * @param the parent that's trying to be set
	 */
	public void setParent(Expression parent) {
		_parent = parent;
	}

	/**
	 * Creates a completely separate copy of the expression tree
	 * @return the new expression tree copy
	 */
	public Expression deepCopy() {
	    Expression copy = new ParsedExpression(new String(_name));

	    for (Expression child : _children) {
	        Expression childCopy = ((ParsedExpression) child).deepCopy();
	        copy.addSubexpression(childCopy);
	    }

	    return copy;
	}
	
	/**
	 * Indents when we're trying to convert the expression tree to string
	 * @param s the string that's being indented
	 * @param indentLevel is which level it's being indented onto
	 */
	public static void indent (StringBuffer s, int indentLevel) {
		for (int i = 0; i < indentLevel; i++) {
			s.append('\t');
		}
	}

	/**
	 * Converts the expression to a string
	 * @param indentLevel is which level it's being indented onto
	 * @return a string depicting the expression tree
	 */
	public String convertToString(int indentLevel) {
		final StringBuffer s = new StringBuffer();
		indent(s, indentLevel);
		s.append(getName() + '\n');
		for(int i = 0; i < _children.size(); i++) {
			s.append(_children.get(i).convertToString(indentLevel+1)); //depth first recursion
		}
		return s.toString();
	}
	
	/**
	 * Adds a subexpression to the big expression tree
	 * @param subexpression is the expression that's being added
	 */
	public void addSubexpression(Expression subexpression) {
		_children.add(subexpression);
		subexpression.setParent(this);
	}
	
	/**
	 * Return the children of the expression
	 * @return the list of children
	 */
	public List<Expression> getChildren() {
		return _children;
	}
	
	/**
	 * Returns the name of this expression, so either a literal or mathematical term
	 * @return the name of the expression
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * Checks to see if the string is a number
	 * @param x the string to check
	 * @return true if x is a number, false otherwise
	 */
	public static boolean isNumber(String x) {
		try {
			Double.parseDouble(x); //tries to parse the string into a double
		}
		catch (NumberFormatException e) {
			return false; //if it throws a NumberFormatException (not a number) then return false
		}
		return true;
	}
	
	/**
	 * Function to check if this expression has a parent
	 * @return true if has parent, false otherwise
	 */
	public boolean hasParent() {
		return _parent != null;
	}
	
	/**
	 * Function to parse through and evaluate the expression
	 * @param x-value of the expression
	 * @return double which represents the y-value of the expression at a given x-value
	 */
	@Override
	public double evaluate(double x) {
		if (isNumber(_name)) {
			return Double.parseDouble(_name);
		}
		if (_name.equals("x")) {
			return x;
		}
		if (_name.equals("+")) {
			if (hasParent() && _parent.getName().equals("-")) {
				return _children.get(0).evaluate(x) - _children.get(1).evaluate(x);
			}
			return _children.get(0).evaluate(x) + _children.get(1).evaluate(x);
		}
		if (_name.equals("-")) {
			if (hasParent() && _parent.getName().equals("-")) {
				return _children.get(0).evaluate(x) + _children.get(1).evaluate(x);
			}
			return _children.get(0).evaluate(x) - _children.get(1).evaluate(x);
		}
		if (_name.equals("*")) {
			if (hasParent() && _parent.getName().equals("/")) {
				return _children.get(0).evaluate(x) / _children.get(1).evaluate(x);
			}
			return _children.get(0).evaluate(x) * _children.get(1).evaluate(x);
		}
		if (_name.equals("/")) {
			return _children.get(0).evaluate(x) / _children.get(1).evaluate(x);
		}
		if (_name.equals("^")) {
			return Math.pow(_children.get(0).evaluate(x), _children.get(1).evaluate(x));
		}
		if (_name.equals("log")) {
			if (_children != null) {
				return Math.log(_children.get(0).evaluate(x));
			}
		}
		if (_name.equals("()")) {
			if (_children != null) {
				return _children.get(0).evaluate(x);
			}
		}
		return 0;
	}

	/**
	 * Function to parse through and evaluate the expression
	 * @param d is the deepCopy of the expression tree
	 * @param a is a boolean with true = add and false = minus
	 * @return expression after differentiating add/minus
	 */
	public Expression diffAddMinus(ParsedExpression d, Boolean a) {
		Expression intA1 = d.getChildren().get(0).differentiate();
    	Expression intB1 = d.getChildren().get(1).differentiate();
		if (a) {
			ParsedExpression add = new ParsedExpression ("+");
			add.addSubexpression(intA1);
	    	add.addSubexpression(intB1);
	        return add;
		}
		else {
			ParsedExpression minus = new ParsedExpression ("-");
	    	minus.addSubexpression(intA1);
	    	minus.addSubexpression(intB1);
	        return minus;
		}
	}
	
	/**
	 * Function to parse through and evaluate the expression
	 * @param d is the deepCopy of the expression tree
	 * @param m is a boolean with true = multiply and false = divide
	 * @return expression after differentiating multiply/divide
	 */
	public Expression diffMultDiv(ParsedExpression d, Boolean m) {
		ParsedExpression add = new ParsedExpression ("+");
		ParsedExpression subtract = new ParsedExpression ("-");
    	ParsedExpression mult1 = new ParsedExpression ("*");
    	ParsedExpression mult2 = new ParsedExpression ("*");
    	ParsedExpression divide = new ParsedExpression ("/");
    	ParsedExpression expo = new ParsedExpression ("^");
    	Expression intAP = d.getChildren().get(0).differentiate(); 		//g'
    	Expression intA1 = d.getChildren().get(0); 						//g1
    	Expression intBP = d.getChildren().get(1).differentiate(); 		//h'
    	Expression intB1 = d.getChildren().get(1); 						//h1
    	Expression intB2 = d.getChildren().get(1); 						//h2
    	mult1.addSubexpression(intAP); //g'
    	mult1.addSubexpression(intB1); //h
    	mult2.addSubexpression(intA1); //g
    	mult2.addSubexpression(intBP); //h'
    	if (m) {
        	add.addSubexpression(mult1);
        	add.addSubexpression(mult2);
        	return add;
    	}
    	else {
	    	subtract.addSubexpression(mult1);
	    	subtract.addSubexpression(mult2);
	    	expo.addSubexpression(intB2);
	    	expo.addSubexpression(new ParsedExpression("2"));
	    	divide.addSubexpression(subtract);
	    	divide.addSubexpression(expo);
	    	return divide;
    	}
	}
	
	/**
	 * Function to parse through and evaluate the expression
	 * @param d is the deepCopy of the expression tree
	 * @param t is type of exponential, true = base is #, false = exponential is #
	 * @return expression after differentiating exponentials
	 */
	public Expression diffExpo (ParsedExpression d, Boolean type) {
		Expression g1 = d.getChildren().get(0); //g
    	Expression g2 = d.getChildren().get(0); //g
    	Expression gP = d.getChildren().get(0).differentiate(); //g'
    	Expression hP = d.getChildren().get(1).differentiate(); //h'
    	Expression h1 = d.getChildren().get(1); //h
    	Expression h2 = d.getChildren().get(1); //h
    	ParsedExpression mult1 = new ParsedExpression ("*");
    	ParsedExpression mult2 = new ParsedExpression ("*");
    	ParsedExpression expo = new ParsedExpression ("^");
    	ParsedExpression log = new ParsedExpression ("log");
    	ParsedExpression sub = new ParsedExpression ("-");
    	if (type) {
    		expo.addSubexpression(g1);
    		expo.addSubexpression(h1);
    		mult1.addSubexpression(hP);
    		mult1.addSubexpression(expo);
    		mult2.addSubexpression(mult1);
    		log.addSubexpression(g2);
    		mult2.addSubexpression(log);
    		return mult2;
    	}
    	else {
    		sub.addSubexpression(h1);
    		sub.addSubexpression(new ParsedExpression ("1"));
    		expo.addSubexpression(g1);
    		expo.addSubexpression(sub);
    		mult1.addSubexpression(gP);
    		mult1.addSubexpression(expo);
    		mult2.addSubexpression(mult1);
    		mult2.addSubexpression(h2);
    		return mult2;
    	}
	}
	
	/**
	 * Function to parse through and evaluate the expression
	 * @param d is the deepCopy of the expression tree
	 * @return expression after differentiating logs
	 */
	public Expression diffLog (ParsedExpression d) {
		ParsedExpression divide = new ParsedExpression ("/");
    	Expression gP = d.getChildren().get(0).differentiate(); //g'
    	Expression g = d.getChildren().get(0); //g
    	divide.addSubexpression(gP);
    	divide.addSubexpression(g);
    	return divide;
	}
	
	/**
	 * Function to parse through and evaluate the expression
	 * @return expression after differentiating
	 */
	@Override
	public Expression differentiate() {
		ParsedExpression derivative = (ParsedExpression) deepCopy();
	    if (derivative.getName().equals("x")) {
	        return new ParsedExpression("1");
	    }
	    if (derivative.getName().equals("+")) {
	        return diffAddMinus(derivative, true);
	    }
	    if (derivative.getName().equals("-")) {
	    	return diffAddMinus(derivative, false);
	    }
	    if (derivative.getName().equals("*")) {
	    	return diffMultDiv(derivative, true);
	    }
	    if (derivative.getName().equals("/")) {
	    	return diffMultDiv(derivative, false);
	    }
	    if (derivative.getName().equals("^")) {
	    	if (isNumber(derivative.getChildren().get(0).getName())) {
	    		return diffExpo(derivative, true);
	    	}
	    	if (isNumber(derivative.getChildren().get(1).getName())) {
	    		return diffExpo(derivative, false);
	    	}
	    }
	    if (derivative.getName().equals("log")) {
	    	return diffLog(derivative);
	    }
	    return new ParsedExpression("0");
	}
}