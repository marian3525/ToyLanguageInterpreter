package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;

import java.io.IOException;

import static model.expression.AbstractExpression.getExpressionFromType;
import static model.expression.AbstractExpression.getExpressionType;

public class WhileStatement extends AbstractStatement {
    private AbstractExpression condition;
    private AbstractStatement statement;
    private String functionName;

    public WhileStatement(AbstractExpression expression, AbstractStatement statement) {
        this.condition = expression;
        this.statement = statement;
    }

    /**
     * Syntax: while(condition): statement
     *
     * @param input
     * @return
     */
    public static WhileStatement getWhileExpressionFromString(String input) throws SyntaxException {
        AbstractExpression condition;
        AbstractStatement statement;
        //remove spaces

        //extract the condition string
        //while(condition): statement
        String[] aux = input.split("\\):");
        String condStr = aux[0].replace(" ", "").replace("while(", "");
        String statementStr = aux[1];

        condition = getExpressionFromType(condStr, getExpressionType(condStr));
        statement = getStatementFromString(statementStr);

        WhileStatement whileStatement = new WhileStatement(condition, statement);
        return whileStatement;
    }

    @Override
    public String toString() {
        return null;
    }

    /**
     * While the condition is true push statement onto the stack. If false, pop the while statement off the stack
     *
     * @param programState
     * @return
     * @throws UndefinedOperationException
     * @throws UndefinedVariableException
     * @throws IOException
     * @throws SyntaxException
     */
    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {
        if (condition.evaluate(programState.getSymbols(), programState.getHeap()) == 0) {
            //condition is false, don't add the statement to the stack
            //the while statement will be popped after this call to execute does nothing
        } else {
            //condition is true, push a statement onto the stack
            programState.getExecutionStack().push(statement);
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
