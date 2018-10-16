package model.expression;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;

import java.util.Map;
import java.util.Stack;
import java.util.Vector;

public abstract class Expression {

    public abstract String toString();

    public abstract int evaluate(Map<String, Integer> symbols) throws UndefinedOperationException, UndefinedVariableException;

    /**
     * @param input : syntactically valid expression in infix notation
     * @return  the postfix representation of the given input expression
     */
    public static Vector<String> infixToPostfix(String input) throws SyntaxException {
        Stack<String> stack = new Stack<>();
        Vector<String> output = new Vector<>(10);
        // initializing empty String for result
        String result = "";

        for (int i = 0; i<input.length(); ++i)
        {
            char c = input.charAt(i);

            // If the scanned character is an operand, add it to output.
            if (Character.isLetterOrDigit(c))
                output.add(String.valueOf(c));

                // If the scanned character is an '(', push it to the stack.
            else if (c == '(')
                stack.push(String.valueOf(c));

                //  If the scanned character is an ')', pop and output from the stack
                // until an '(' is encountered.
            else if (c == ')')
            {
                while (!stack.isEmpty() && !stack.peek().equals(String.valueOf('(')))
                    output.add(stack.pop());

                if (!stack.isEmpty() && !stack.peek().equals(String.valueOf('(')))
                    throw new SyntaxException("Malformed expression: " + input + " @ character: " + input.charAt(i));
                else
                    stack.pop();
            }
            else // an operator is encountered
            {
                while (!stack.isEmpty() && priority(c) <= priority(stack.peek().charAt(0)))
                    output.add(stack.pop());
                stack.push(String.valueOf(c));
            }
        }

        // pop all the operators from the stack
        while (!stack.isEmpty())
            output.add(stack.pop());

        return output;
    }

    /**
     *
     * @param ch: operator to check
     * @return The priority order of the operator
     */
    private static int priority(char ch)
    {
        switch (ch)
        {
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
     *
     * @param postfix : Vector of strings representing token of the expression in postfix notation
     * @return An Expression built from the postfix expression
     */
    public static ArithmeticExpression buildExpressionFromPostfix(Vector<String> postfix) {
        Stack<Expression> stack = new Stack<>();

        for(String tok : postfix) {
            //if it is a variable or a constant, push it to the stack
            if(!tok.matches("[-+*/|\\s*]")) {
                //convert the token tok to the appropriate type of expression
                Expression exp = convertStringToExpression(tok);
                stack.push(exp);
            }
            else {
                //if it is an operator, pop 2 from the stack, create a new expression using the operator
                //and push it back to the stack

                Expression second = stack.pop();
                Expression first = stack.pop();
                Expression combined = new ArithmeticExpression(first, second, tok);
                stack.push(combined);
            }
        }
        //in the end, the full expression should be the only one in the stack
        return (ArithmeticExpression) stack.pop();
    }

    /**
     *
     * @param tok a token
     * @return
     */
    private static Expression convertStringToExpression(String tok) {
        //if it contains letters, it must be a variable with tok as name
        Expression output = null;
        if(tok.matches("[[a-z]*]")) {
            output = new VariableExpression(tok);
        }
        //if it is a constant value
        else if(tok.matches("[[1-9][0-9]*]")) {
            output = new ConstantExpression(Integer.parseInt(tok));
        }
        return output;
    }

}
