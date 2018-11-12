package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static model.expression.AbstractExpression.getExpressionFromType;
import static model.expression.AbstractExpression.getExpressionType;

public class NewHeapEntryStatement extends AbstractStatement {

    private String varName;
    private AbstractExpression expression;
    private String functionName;

    public NewHeapEntryStatement(String varName, AbstractExpression expression) {
        this.varName = varName;
        this.expression = expression;
    }

    /**
     * Syntax: new(varName_addr, expr)
     *
     * @param input
     * @return
     * @throws SyntaxException
     */
    static NewHeapEntryStatement getNewHeapEntryStatementFromString(@NotNull String input) throws SyntaxException {
        String varName;
        AbstractExpression expr;
        String[] params = input.replace("new(", "")
                .replace(")", "")
                .replace(" ", "").split(",");
        varName = params[0];

        expr = getExpressionFromType(params[1], getExpressionType(params[1]));

        NewHeapEntryStatement statement = new NewHeapEntryStatement(varName, expr);

        return statement;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        int key = programState.getSymbols().get(varName);
        int value = expression.evaluate(programState.getSymbols(), programState.getHeap());

        programState.getHeap().put(key, value);

        return programState;
    }

    @Override
    public String getFunction() {
        return functionName;
    }

    @Override
    public void setFunction(String functionName) {
        this.functionName = functionName;
    }
}
