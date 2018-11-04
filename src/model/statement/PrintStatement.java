package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;

import java.io.IOException;

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

    @Override
    public String toString() {
        return "print(" + expression.toString() + ")";
    }
    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException {
        programState.getOutput().add(Integer.toString(expression.evaluate(programState.getSymbols())));
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
