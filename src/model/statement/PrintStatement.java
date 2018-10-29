package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.Expression;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;

import java.io.IOException;

public class PrintStatement extends Statement {
    private Expression expression;
    @RegExp
    public static final String printRegex = "";

    public PrintStatement(Expression expression) {
        this.expression = expression;
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
}
