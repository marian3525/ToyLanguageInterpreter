package model.expression;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.interfaces.HeapInterface;
import parsers.ExpressionParser;

import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

public class BooleanExpression extends AbstractExpression {
    private AbstractExpression first;
    private AbstractExpression second;
    private String operator;
    private static String[] ops = {"<", "<=", "==", ">=", ">", "!="};
    private static Vector<String> validOperators = new Vector<>(Arrays.asList(ops));

    public BooleanExpression(AbstractExpression first, AbstractExpression second, String operator) {
        this.first = first;
        this.second = second;
        this.operator = operator;
    }

    @SuppressWarnings("unchecked")
    public static Vector<String> getValidOperators() {
        return (Vector<String>) validOperators.clone();
    }

    /**
     * Build a boolean expression from the given string
     *
     * @param expressionStr input string
     * @return an instance of the BooleanExpression built from the given string
     */
    public static BooleanExpression buildBooleanExpressionFromString(String expressionStr) throws SyntaxException {
        StringBuilder operatorBuilder = new StringBuilder();
        String operator = "";
        StringBuilder left = new StringBuilder();
        StringBuilder right = new StringBuilder();
        boolean inLhs = true;   //starting in the lhs of the statement
        expressionStr = expressionStr.replace(" ", "");
        int i = 0;
        //extract the operator
        while (i < expressionStr.length()) {
            if ("<=>!".contains(Character.toString(expressionStr.charAt(i)))) {
                //if an operator or part of an operator, add it to the op. string
                operatorBuilder.append(expressionStr.charAt(i));

                if (!"<=>!".contains(Character.toString(expressionStr.charAt(i + 1)))) {
                    //end of a single operator operator string
                    operator = operatorBuilder.toString();
                    inLhs = false;
                }

            }
            else {
                //if expression...
                if (inLhs) {
                    //in the lhs
                    left.append(expressionStr.charAt(i));
                }
                else {
                    //rhs
                    right.append(expressionStr.charAt(i));
                }
            }
            i++;
        }
        AbstractExpression first = ExpressionParser.getExpressionFromString(left.toString());
        AbstractExpression second = ExpressionParser.getExpressionFromString(right.toString());
        BooleanExpression e = new BooleanExpression(first, second, operator);
        return e;
    }

    @Override
    public String toString() {
        return first.toString() + " " + operator + " " + second.toString();
    }

    @Override
    public int evaluate(Map<String, Integer> symbols, HeapInterface heap) throws UndefinedOperationException, UndefinedVariableException, SyntaxException {

        switch (operator) {
            case "<":
                //r = () -> first.evaluate(symbols, heap) < second.evaluate(symbols, heap) ? 1 : 0;
                return first.evaluate(symbols, heap) < second.evaluate(symbols, heap) ? 1 : 0;
            case "<=":
                return first.evaluate(symbols, heap) <= second.evaluate(symbols, heap) ? 1 : 0;
            case "==":
                return first.evaluate(symbols, heap) == second.evaluate(symbols, heap) ? 1 : 0;
            case "!=":
                return first.evaluate(symbols, heap) != second.evaluate(symbols, heap) ? 1 : 0;
            case ">":
                return first.evaluate(symbols, heap) > second.evaluate(symbols, heap) ? 1 : 0;
            case ">=":
                return first.evaluate(symbols, heap) >= second.evaluate(symbols, heap) ? 1 : 0;
            case "AND":
                return first.evaluate(symbols, heap) != 0 && second.evaluate(symbols, heap) != 0 ? 1 : 0;
            case "OR":
                return first.evaluate(symbols, heap) != 0 || second.evaluate(symbols, heap) != 0 ? 1 : 0;
        }
        return 0;
    }

}
