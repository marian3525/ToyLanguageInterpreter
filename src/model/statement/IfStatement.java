package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;
import parsers.ExpressionParser;
import parsers.StatementParser;

public class IfStatement extends AbstractStatement {
    private AbstractExpression condition;
    private AbstractStatement thenStatement;
    private AbstractStatement elseStatement;
    private String functionName;

    //match expressions like: if cond then expr1 else expr2
    //else branch is optional
    @RegExp
    private static final String ifStatementRegex = "^if\\s[^\\s]+\\sthen\\s[^\\s]+(\\selse\\s[^\\s]+)?$";

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

    /**
     * syntax expected: if expr/var/const then statement1 else statement2;
     * statement1 and statement2 can be compound statements, so don't remove the ; yet
     *
     * @param input: if statement string which is syntactically valid
     * @return an IfStatement parsed from the given string
     */
    public static IfStatement getIfStatementFromString(String input) throws SyntaxException {
        AbstractExpression condition;
        AbstractStatement thenStatement;
        AbstractStatement elseStatement;
        String conditionType;
        String thenStatementType;
        String elseStatementType;
        String[] tokens = input.split(" ");

        //condition at pos. 1
        // thenStatement at 3
        // elseStatement at 5
        //elseStatementType = getStatementType(tokens[5].replace(";", ""));   //it would replace the ;
        //in compound statements from the else branch as well

        condition = ExpressionParser.getExpressionFromString(tokens[1]);
        thenStatement = StatementParser.getStatementFromString(tokens[3]);
        elseStatement = StatementParser.getStatementFromString(tokens[5]);

        IfStatement ifStatement = new IfStatement(condition, thenStatement, elseStatement);
        return ifStatement;
    }

    @Override
    public String toString() {
        return "if " + condition.toString() + " then " + thenStatement.toString() + " else " +
                elseStatement.toString() + ";";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, SyntaxException {
        if (condition.evaluate(programState.getSymbols(), programState.getHeap()) != 0) {
            programState.getExecutionStack().push(thenStatement);
        }
        else {
            programState.getExecutionStack().push(elseStatement);
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
        return statementString.matches(ifStatementRegex);
    }
}
