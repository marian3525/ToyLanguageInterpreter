package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import parsers.ExpressionParser;

public class NewHeapEntryStatement extends AbstractStatement {

    @RegExp
    private static final String newHeapEntryStatementRegex = "^new\\(.*\\)$";
    private String varName;
    private AbstractExpression expression;
    private String functionName;

    public NewHeapEntryStatement(String varName, AbstractExpression expression) {
        this.varName = varName;
        this.expression = expression;
    }

    /**
     * Syntax: new(varName_addr, expr)
     *  Allocate a space in the heap, put the value of expr in the space and return the address of it
     * @param input
     * @return
     * @throws SyntaxException
     */
    public static NewHeapEntryStatement getNewHeapEntryStatementFromString(@NotNull String input) throws SyntaxException {
        String varName;
        AbstractExpression expr;
        String[] params = input.replace("new(", "")
                .replace(")", "")
                .replace(" ", "").split(",");
        varName = params[0];

        expr = ExpressionParser.getExpressionFromString(params[1]);
        NewHeapEntryStatement statement = new NewHeapEntryStatement(varName, expr);

        return statement;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException {
        int value = expression.evaluate(programState.getSymbols(), programState.getHeap());

        int addr = programState.getHeap().put(value);
        programState.getSymbols().put(varName, addr);

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

    @Override
    public String toString() {
        return "new(" + varName + "," + expression.toString() + ")";
    }

    /**
     * Allocate a space in the heap, put the value of expr in the space and return the address of it
     *
     * @param programState
     * @return
     * @throws UndefinedOperationException
     * @throws UndefinedVariableException
     */
    /**
     * Check if the given string matches the structure of the statement described by this class
     * @param statementString string to be checked
     * @return true if the class can parse the string and output an object of this type
     *          false if the string doesn't match the class
     */
    public static boolean matchesString(String statementString) {
        return statementString.matches(newHeapEntryStatementRegex);
    }
}
