package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;
import parsers.ExpressionParser;
import parsers.StatementParser;

public class WhileStatement extends AbstractStatement {
    @RegExp
    private static final String whileStatementRegex = "^while\\(.*\\):.*$";
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
        String statementStr = aux[1].replace(" ", "");

        condition = ExpressionParser.getExpressionFromString(condStr);
        statement = StatementParser.getStatementFromString(statementStr);

        WhileStatement whileStatement = new WhileStatement(condition, statement);
        return whileStatement;
    }

    @Override
    public String toString() {
        return "while(" + condition.toString() + "): " + statement.toString();
    }

    /**
     * While the condition is true push statement onto the stack. If false, pop the while statement off the stack
     *
     * @param programState
     * @return
     * @throws UndefinedOperationException
     * @throws UndefinedVariableException
     */
    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, SyntaxException {
        if (condition.evaluate(programState.getSymbols(), programState.getHeap()) == 0) {
            //condition is false, don't add the statement to the stack
            //the while statement will be popped after this call to execute does nothing

        } else {
            //condition is true, push a statement onto the stack
            programState.getExecutionStack().push(this);
            programState.getExecutionStack().push(statement);
        }
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
        return statementString.matches(whileStatementRegex);
    }
}
