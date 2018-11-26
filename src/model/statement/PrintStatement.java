package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;
import parsers.ExpressionParser;

import java.io.IOException;

public class PrintStatement extends AbstractStatement {
    private AbstractExpression expression;
    @RegExp
    private static final String printStatementRegex = "^print\\(.*\\)$";
    private String functionName;

    public PrintStatement(AbstractExpression expression) {
        this.expression = expression;
        this.functionName = "main";
    }

    public PrintStatement(AbstractExpression expression, String functionName) {
        this.expression = expression;
        this.functionName = functionName;
    }

    /**
     * Syntax: print(<expr>)
     *
     * @param input string representation of a print statement
     * @return A PrintStatement build from the input string
     * @throws SyntaxException if the input string is not syntactically valid
     */
    public static PrintStatement getPrintStatementFromString(String input) throws SyntaxException {
        //depending on the parameter, treat each case
        //syntax: print(a) OR print(a+2*b) OR print(2)
        //          var         expr            const
        //extract the expression string
        AbstractExpression expr = null;
        //replace all instead of replace so that a regex is used to replace only the last ')' from the print
        String param = input.replace("print(", "").replaceAll("\\)$", "").replace(";", "");
        //added so the param wouldn't have a space in front of it in print(i)
        param = param.replace(" ", "");
        //figure out what kind of expression param is:
        expr = ExpressionParser.getExpressionFromString(param);

        PrintStatement s = new PrintStatement(expr);
        return s;
    }

    @Override
    public String toString() {
        return "print(" + expression.toString() + ")";
    }
    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException {
        programState.getOutput().add(Integer.toString(expression.evaluate(programState.getSymbols(), programState.getHeap())));
        return null;
    }

    @Override
    public String getFunction() {
        return null;
    }

    @Override
    public void setFunction(String functionName) {
        this.functionName = functionName;
    }

    /**
     * Check if the given string matches the structure of the statement described by this class
     * @param statementString string to be checked
     * @return true if the class can parse the string and output an object of this type
     *          false if the string doesn't match the class
     */
    public static boolean matchesString(String statementString) {
        return statementString.matches(printStatementRegex);
    }
}
