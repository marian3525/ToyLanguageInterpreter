package model.expression;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import org.jetbrains.annotations.NotNull;

import java.util.EmptyStackException;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;


public abstract class AbstractExpression {

    public abstract String toString();

    public abstract int evaluate(Map<String, Integer> symbols) throws UndefinedOperationException, UndefinedVariableException;

    /**
     * Split an expression into its elements and put them into a vector. E.g. a+(22-13*b) -> a,+,(,22,-,13,*,b,)
     *
     * @param in: string work with
     * @return Vector containing the variables, constants and operators from the input string
     */
    public static Vector<String> tokenize(String in) {
        Vector<String> out = new Vector<>(10);
        StringBuilder nameBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();

        //add a space at the end of the string so that next doesn't go out of bounds
        in = in.concat(" ");

        for (int i = 0; i < in.length()-1; i++) {
            char current = in.charAt(i);
            char next = in.charAt(i + 1);

            if ("()".contains(String.valueOf(current))) {
                out.add(String.valueOf(current));
            }
            else if("+-*/".contains(String.valueOf(current))) {
                out.add(String.valueOf(current));
            }
            //if a letter, start building a var name
            else if (Character.isLetterOrDigit(current) && nameBuilder.length() > 0 || Character.isLetter(current)) {
                    nameBuilder.append(current);
                    if (!Character.isLetterOrDigit(next)) {
                        //end of var name, build the String
                        //nameBuilder.append(next);
                        out.add(nameBuilder.toString());
                        nameBuilder.delete(0, nameBuilder.length());
                }
            }
            //number constants
            else if(Character.isDigit(current)) {
                    valueBuilder.append(current);
                    if(!Character.isDigit(next)) {
                        //valueBuilder.append(next);
                        out.add(valueBuilder.toString());
                        valueBuilder.delete(0, valueBuilder.length());
                }
            }
        }

        return out;
    }

    /**
     * @param input : syntactically valid expression in infix notation
     * @return the postfix representation of the given input expression
     */
    public static Vector<String> infixToPostfix(String input) throws SyntaxException {
        Stack<String> stack = new Stack<>();
        Vector<String> output = new Vector<>(10);
        // initializing empty String for result
        String result = "";
        //convert the input string into a vector of variables and constants
        Vector<String> in = tokenize(input);

        for (int i = 0; i < in.size(); i++) {
            String c = in.elementAt(i);

            // If the scanned character is an operand, add it to output.
            if (Character.isLetterOrDigit(c.charAt(0)))
                output.add(String.valueOf(c));

                // If the scanned character is an '(', push it to the stack.
            else if (c.equals("("))
                stack.push(c);

                //  If the scanned character is an ')', pop and output from the stack
                // until an '(' is encountered.
            else if (c.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("("))
                    output.add(stack.pop());

                if (!stack.isEmpty() && !stack.peek().equals("("))
                    throw new SyntaxException("Malformed expression: " + in + " @ character: " + in.elementAt(i));
                else
                    stack.pop();
            } else // an operator is encountered
            {
                while (!stack.isEmpty() && priority(c.charAt(0)) <= priority(stack.peek().charAt(0)))
                    output.add(stack.pop());
                stack.push(String.valueOf(c));
            }
        }

        // pop all the operators from the stack
        while (!stack.isEmpty())
            output.add(stack.pop());

        return output;
    }

    public static Vector<String> postfixToInfix(Vector<String> postfix) {
        Vector<String> output = new Vector<>();
        Stack<String> stack = new Stack<>();

        return output;
    }

    /**
     * @param ch: operator to check
     * @return The priority order of the operator
     */
    private static int priority(char ch) {
        switch (ch) {
            case '+':
            case '-':
                return 1;

            case '*':
            case '/':
                return 2;

            case '^':
                return 3;
        }
        return -1;
    }

    /**
     * @param postfix : Vector of strings representing token of the expression in postfix notation
     * @return An AbstractExpression built from the postfix expression
     */
    public static ArithmeticExpression buildExpressionFromPostfix(Vector<String> postfix) {
        Stack<AbstractExpression> stack = new Stack<>();

        for (String tok : postfix) {
            //if it is a variable or a constant, push it to the stack
            if (!tok.matches("[-+*/|\\s*]")) {
                //convert the token tok to the appropriate type of expression
                AbstractExpression exp = convertStringToExpression(tok);
                stack.push(exp);
            } else {
                //if it is an operator, pop 2 from the stack, create a new expression using the operator
                //and push it back to the stack7
                AbstractExpression second = null;
                AbstractExpression first = null;
                try {
                    second = stack.pop();
                    first = stack.pop();
                } catch (EmptyStackException e) {
                    //throw new ProgramException("Error parsing expressin");
                }
                AbstractExpression combined = new ArithmeticExpression(first, second, tok);
                stack.push(combined);
            }
        }
        if (stack.peek() instanceof ConstantExpression) {
            ConstantExpression c = (ConstantExpression) stack.pop();
            return new ArithmeticExpression(c, new ConstantExpression(0), "+");
        }
        //in the end, the full expression should be the only one in the stack
        return (ArithmeticExpression) stack.pop();
    }

    /**
     * @param tok a token string
     * @return AbstractExpression built from the string
     */
    private static AbstractExpression convertStringToExpression(String tok) {
        //if it contains letters, it must be a variable with tok as name
        AbstractExpression output = null;
        if (tok.matches("[a-z]*")) {
            output = new VariableExpression(tok);
        }
        //if it is a constant value
        else if (tok.matches("[1-9][0-9]*")) {
            output = new ConstantExpression(Integer.parseInt(tok));
        }
        return output;
    }

    /**
     * Find the type of expression which fits the given semantics
     *
     * @param expression: expression whose type needs to be identified
     * @return: the type of expression as class
     */
    @NotNull
    public static String getExpressionType(String expression) {
        if (expression.matches(ConstantExpression.constantRegex)) {
            return "ConstantExpression";
        } else if (expression.matches(VariableExpression.variableRegex)) {
            return "VariableExpression";
        } else {
            return "ArithmeticExpression";
        }
    }

    /**
     * @param expressionStr  string representation of an expression
     * @param expressionType type of expression: (constant, variable or arithmetic)
     * @return An AbstractExpression built from the given string
     * @throws SyntaxException if there are syntax errors in the input string
     */
    public static AbstractExpression getExpressionFromType(String expressionStr, String expressionType) throws SyntaxException {
        switch (expressionType) {
            case "ConstantExpression":
                return new ConstantExpression(Integer.parseInt(expressionStr));
            case "VariableExpression":
                String varName = expressionStr.split("=")[0];
                return new VariableExpression(varName);
            case "ArithmeticExpression":
                String rhs = expressionStr;//.split("=")[1];       //crash on print(a+1)
                Vector<String> postfix = AbstractExpression.infixToPostfix(rhs);
                return AbstractExpression.buildExpressionFromPostfix(postfix);
        }
        return null;
    }
}
