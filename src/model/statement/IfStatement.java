package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;

import java.io.IOException;

public class IfStatement extends AbstractStatement {
    private AbstractExpression condition;
    private AbstractStatement thenStatement;
    private AbstractStatement elseStatement;
    private String functionName;
    @RegExp
    public static final String ifRegex = "";

    public IfStatement(AbstractExpression condition, AbstractStatement thenStatement, AbstractStatement elseStatement) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
        this.functionName = "main";
    }

    public IfStatement(AbstractExpression condition, AbstractStatement thenStatement, AbstractStatement elseStatement,
                       String functionName) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
        this.functionName = functionName;
    }

    @Override
    public String toString() {
        return "if " + condition.toString() + " then " + thenStatement.toString() + " else " +
                elseStatement.toString() + ";";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException {
        if(condition.evaluate(programState.getSymbols()) != 0) {
            programState.getExecutionStack().push(thenStatement);
        }
        else {
            programState.getExecutionStack().push(elseStatement);
        }
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
