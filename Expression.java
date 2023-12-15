//import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
//import javafx.scene.Node;
package application;

import java.util.List;

/** Note: You may <b>not</b> change or remove any of the methods listed below. */
interface Expression {
	/**
	 * Creates and returns a deep copy of the expression.
	 * The entire tree rooted at the target node is copied, i.e.,
	 * the copied Expression is as deep as possible.
	 * @return the deep copy
	 */
	Expression deepCopy ();

	/**
	 * Creates a String representation of this expression with a given starting
	 * indent level. If indentLevel is 0, then the produced string should have no
	 * indent; if the indentLevel is 1, then there should be 1 tab '\t'
	 * character at the start of every line produced by this method; etc.
	 * @param indentLevel how many tab characters should appear at the beginning of each line.
	 * @return the String representing this expression.
	 */
	public String convertToString (int indentLevel);

	/**
	 * Given the value of the independent variable x, compute the value of this expression.
	 * @param x the value of the independent variable x
	 * @return the value of this expression.
	 */
	public double evaluate (double x);

	/**
	 * Produce a new, fully independent (i.e., there should be no shared subtrees) Expression
	 * representing the derivative of this expression.
	 * @return the derivative of this expression
	 */
	public Expression differentiate ();
	
	/**
	 * Getter for name
	 * @return the name of the expression
	 */
	String getName();
	
	/**
	 * Sets the parent of the subexpression after you add it to the overall expression tree
	 */
	void setParent (Expression parent);
    	
	/**
	 * Adds a subexpression to the overall expression tree
	 */
	void addSubexpression (Expression child);

	/**
	 * Getter for children
	 * @return the list of children associated to that parent
	 */
	List<Expression> getChildren();
}
