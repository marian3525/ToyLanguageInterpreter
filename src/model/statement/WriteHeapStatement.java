package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.adt.Heap;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;
import parsers.ExpressionParser;

public class WriteHeapStatement extends AbstractStatement {
    @RegExp
    private static final String writeHeapStatementRegex = "^writeHeap\\(.*\\)$";
    private String varName;
    private String functionName;
    private AbstractExpression expression;

    public WriteHeapStatement(String varName, AbstractExpression expression) {
        this.varName = varName;
        this.expression = expression;
    }

    /**
     * Syntax: write(varName_addr,expr)
     *
     * @param statement
     * @return
     */
    public static WriteHeapStatement getWriteHeapStatementFromString(String statement) throws SyntaxException {
        statement = statement.replace("writeHeap(", "");
        statement = statement.replace(")", "");
        statement = statement.replace(" ", "");

        String[] params = statement.split(",");
        String varName = params[0];

        String exprString = params[1];
        AbstractExpression expr = ExpressionParser.getExpressionFromString(exprString);

        WriteHeapStatement writeHeapStatement = new WriteHeapStatement(varName, expr);
        return writeHeapStatement;
    }

    @Override
    public String toString() {
        return "writeHeap(" + varName+")";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, SyntaxException {
        Heap h = (Heap) programState.getHeap();

        if (h.get(programState.getSymbols().get(varName)) != null) {
            // put the value of expression.evaluate() at position given by varName
            h.put(programState.getSymbols().get(varName), expression.evaluate(
                    programState.getSymbols(), programState.getHeap()
            ));
            programState.getHeap().setContent(h.getContent());
        } else
            throw new UndefinedOperationException("Cannot write to address not yet allocated");
        return null;
    }

    @Override
    public String getFunction() {
        return functionName;
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
        return statementString.matches(writeHeapStatementRegex);
    }
}
