package parsers;

import exceptions.SyntaxException;
import model.expression.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ExpressionParser {
    /**
     * @param expressionStr string representation of an expression
     * @return An AbstractExpression built from the given string
     * @throws SyntaxException if there are syntax errors in the input string
     */
    public static AbstractExpression getExpressionFromString(String expressionStr)
            throws SyntaxException {
        ExpressionType expressionType = getExpressionType(expressionStr);
        switch (expressionType) {
            case ConstantExp:
                return new ConstantExpression(Integer.parseInt(expressionStr));
            case VariableExp:
                String varName = expressionStr.split("=")[0];
                return new VariableExpression(varName);
            case ArithmeticExp:
                String rhs = expressionStr;//.split("=")[1];       //crash on print(a+1)
                Vector<String> postfix = infixToPostfix(rhs);
                return buildExpressionFromPostfix(postfix);
            case BooleanExp:
                return BooleanExpression.buildBooleanExpressionFromString(expressionStr);
            case ReadHeapExp:
                varName = expressionStr.split("\\(")[1].replace(")", "");
                return new ReadHeapExpression(varName);
            case DereferenceExpression:
                String key = expressionStr.replace("*", "");
                return new DereferenceExpression(key);
        }
        return null;
    }

    /**
     * Split an expression into its elements and put them into a vector. E.g. a+(22-13*b) -> a,+,(,22,-,13,*,b,)
     * If function calls are included, they should have the syntax: <functionName>(params1, param2...paramn)
     *
     * @param in: string work with
     * @return Vector containing the variables, constants and operators from the input string
     */
    public static Vector<String> tokenize(String in) {
        Vector<String> out = new Vector<>(10);
        StringBuilder nameBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        String[] ops = {"+", "-", "/", "*", "<", "<=", "==", ">=", ">", "!="};
        Vector<String> operators = new Vector<>(Arrays.asList(ops));
        //add a space at the end of the string so that next doesn't go out of bounds
        in = in.concat(" ");

        //go over the string with current and next = current+1
        for (int i = 0; i < in.length() - 1; i++) {
            char current = in.charAt(i);
            char next = in.charAt(i + 1);

            if ("()".contains(String.valueOf(current))) {
                out.add(String.valueOf(current));
            }
            // 1 char operator
            else if (operators.contains(String.valueOf(current))) {
                out.add(String.valueOf(current));
            }
            // 2 char operator
            else if (operators.contains(new StringBuilder().append(current).append(next).toString())) {
                out.add(String.valueOf(current) + next);
            }
            //if a letter, start building a var name OR FUNCTION NAME!
            else if (Character.isLetterOrDigit(current) && nameBuilder.length() > 0 || Character.isLetter(current)) {
                nameBuilder.append(current);
                if (!Character.isLetterOrDigit(next)) {
                    //end of var name
                    //check if it a function by checking for '('
                    //if a '(' follows a char, it must be a function name
                    if (next == '(') {
                        //it is a function, get the params and add them to the end of the function
                        while (current != ')') {

                            i++;
                            current = in.charAt(i);

                            nameBuilder.append(current);
                        }
                    }
                    //nameBuilder.append(next);
                    out.add(nameBuilder.toString());
                    nameBuilder.delete(0, nameBuilder.length());
                }
            }
            //number constants
            else if (Character.isDigit(current)) {
                valueBuilder.append(current);
                if (!Character.isDigit(next)) {
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

        //convert the input string into a vector of variables and constants
        Vector<String> in = tokenize(input);

        for (String c : in) {
            //String c = in.elementAt(i);

            // If the scanned character is an operand, add it to output.
            if (Character.isLetterOrDigit(c.charAt(0)))
                output.add(c);
                //if it is a function call, add the string
            else if (c.matches(ReadHeapExpression.readHeapExpressionRegex))
                output.add(c);
                // If the scanned character is an '(', push it to the stack.
            else if (c.equals("("))
                stack.push(c);

                //  If the scanned character is an ')', pop and output from the stack
                // until an '(' is encountered.
            else if (c.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("("))
                    output.add(stack.pop());

                if (!stack.isEmpty() && !stack.peek().equals("("))
                    throw new SyntaxException("Malformed expression: " + in + " @ character: " + c);
                else if (!stack.empty())
                    stack.pop();
                else
                    throw new SyntaxException("Malformed expression: " + in + " @ character: " + c);
            }
            else // an operator is encountered
            {
                while (!stack.isEmpty() && priority(c) <= priority(stack.peek()))
                    output.add(stack.pop());
                stack.push(c);
            }
        }

        // pop all the operators from the stack
        while (!stack.isEmpty())
            output.add(stack.pop());

        return output;
    }

    /**
     * @param ch: operator to check
     * @return The priority order of the operator
     */
    private static int priority(String ch) {
        switch (ch) {
            case ">":
            case ">=":
            case "==":
            case "<=":
            case "<":
            case "!=":
                return 0;
            case "+":
            case "-":
                return 1;

            case "*":
            case "/":
                return 2;

            case "^":
                return 3;
        }
        return -1;
    }

    /**
     * @param postfix : Vector of strings representing token of the expression in postfix notation
     * @return An AbstractExpression built from the postfix expression
     */
    public static AbstractExpression buildExpressionFromPostfix(Vector<String> postfix) throws SyntaxException {
        Stack<AbstractExpression> stack = new Stack<>();

        for (String tok : postfix) {
            //if it is a variable or a constant, push it to the stack
            if (!tok.matches("[-+*/><(<=)(>=)(==)|\\s*]")) {
                //convert the token tok to the appropriate type of expression
                AbstractExpression exp = convertStringToExpression(tok);
                stack.push(exp);
            }
            else {
                //if it is an operator, pop 2 from the stack, create a new expression using the operator
                //and push it back to the stack7
                AbstractExpression second = null;
                AbstractExpression first = null;
                try {
                    second = stack.pop();
                    first = stack.pop();
                } catch (EmptyStackException e) {
                    //throw new ProgramException("Error parsing expression");
                }
                AbstractExpression combined = null;
                if (BooleanExpression.getValidOperators().contains(tok)) {
                    combined = new BooleanExpression(first, second, tok);
                }
                else if (ArithmeticExpression.getValidOperators().contains(tok)) {
                    combined = new ArithmeticExpression(first, second, tok);
                }
                stack.push(Objects.requireNonNull(combined));
            }
        }

        if (stack.peek() instanceof ConstantExpression) {
            ConstantExpression c = (ConstantExpression) stack.pop();
            return new ArithmeticExpression(c, new ConstantExpression(0), "+");
        }
        if (stack.peek() instanceof BooleanExpression) {
            BooleanExpression b = (BooleanExpression) stack.pop();
            return b;
        }
        //in the end, the full expression should be the only one in the stack
        ArithmeticExpression e = (ArithmeticExpression) stack.pop();
        return e;
    }

    /**
     * Convert a string to a AbstractExpression type object
     *
     * @param tok a token string
     * @return AbstractExpression built from the string
     */
    private static AbstractExpression convertStringToExpression(String tok) throws SyntaxException {
        //if it contains letters, it must be a variable with tok as name
        //todo: all but the last output assignment redundant?
        AbstractExpression output = null;
        if (tok.matches(VariableExpression.variableRegex)) {
            output = new VariableExpression(tok);
        }
        //if it is a constant value
        else if (tok.matches("[1-9][0-9]*")) {
            output = new ConstantExpression(Integer.parseInt(tok));
        }
        else if (tok.matches(ReadHeapExpression.readHeapExpressionRegex)) {
            output = getExpressionFromString(tok);
        }
        return output;
    }

    /**
     * Better version of figuring out what kind of expression the string represents
     * Instead of checking for the existence of some tokens specific to an expression type,
     * check the last operation to be executed from the postfix notation. This way, (1<3)+2 will be
     * evaluated as arithExpression and 1<3+2 will be eval. as booleanExpression even if they both contain
     * the same tokens
     *
     * @param expression: input expression string
     * @return the type of expression enum
     */
    @NotNull
    private static ExpressionType getExpressionType(@NotNull String expression) {
        //if the expression can be matched 100% of the time using the regex, don't use the postfix method
        if (ConstantExpression.matchesString(expression)) {
            return ExpressionType.ConstantExp;
        } else if (VariableExpression.matchesString(expression)) {
            return ExpressionType.VariableExp;
        } else if (ReadHeapExpression.matchesString(expression)) {
            return ExpressionType.ReadHeapExp;
        } else if (NotExpression.matchesString(expression)) {
            return ExpressionType.NotExp;
        } else if (DereferenceExpression.matchesString(expression)) {
            return ExpressionType.DereferenceExpression;
        }

        //apply the postfix method only on more complex expressions: boolean and arithmetic exprs
        Vector<String> postfix;
        try {
            postfix = infixToPostfix(expression);
        } catch (SyntaxException se) {
            return ExpressionType.UndefinedExp;
        }
        String lastOp = postfix.lastElement();
        //lastOp will determine the type of expression.
        //e.g. 1 3 < 2 + is arithmetic whereas 1 3 2 + < is boolean
        if (ArithmeticExpression.getValidOperators().contains(lastOp)) {
            //last operation is supported by ArithmeticExpression
            return ExpressionType.ArithmeticExp;
        }
        else if (BooleanExpression.getValidOperators().contains(lastOp)) {
            return ExpressionType.BooleanExp;
        }
        return ExpressionType.UndefinedExp;
    }

    private enum ExpressionType {
        ConstantExp, VariableExp, ArithmeticExp, BooleanExp, NotExp, ReadHeapExp,
        DereferenceExpression, UndefinedExp
    }
}
