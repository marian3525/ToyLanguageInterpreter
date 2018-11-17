package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;

import java.io.IOException;

import static model.expression.AbstractExpression.getExpressionFromType;
import static model.expression.AbstractExpression.getExpressionType;

public class PrintStatement extends AbstractStatement {
    private AbstractExpression expression;
    @RegExp
    public static final String printRegex = "";
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

        //figure out what kind of expression param is:
        expr = getExpressionFromType(param, getExpressionType(param));

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
        return programState;
    }

    @Override
    public String getFunction() {
        return null;
    }

    @Override
    public void setFunction(String functionName) {
        this.functionName = functionName;
    }
}
