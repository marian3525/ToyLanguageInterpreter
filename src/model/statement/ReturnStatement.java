package model.statement;

import exceptions.SyntaxException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import org.jetbrains.annotations.NotNull;
import parsers.ExpressionParser;

public class ReturnStatement extends AbstractStatement {

    private String functionName;
    private AbstractExpression value;

    public ReturnStatement(String functionName, AbstractExpression value) {
        this.functionName = functionName;
        this.value = value;
    }

    public ReturnStatement() {

    }

    public ReturnStatement(AbstractExpression value) {
        this.value = value;
    }

    public ReturnStatement(String functionName) {
        this.functionName = functionName;
        this.value = null;
    }

    /**
     * syntax: return varName or return
     *
     * @param input
     * @return
     */
    public static ReturnStatement getReturnStatementFromString(@NotNull String input) throws SyntaxException {
        ReturnStatement statement = null;
        if (input.split(" ").length == 1) {
            //no return varName
            statement = new ReturnStatement();
        } else if (input.split(" ").length == 2) {
            //has return varName
            statement = new ReturnStatement(ExpressionParser.getExpressionFromString(input.split(" ")[0]
            ));
        }
        return statement;
    }

    @Override
    public String toString() {
        return "Return from: " + functionName + " with " + value;
    }

    @Override
    public ProgramState execute(ProgramState programState) {
        //flag the end of the execution
        programState.setFunctionFinished(true);
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
