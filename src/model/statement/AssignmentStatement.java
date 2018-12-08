package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import parsers.ExpressionParser;

import java.util.Map;
import java.util.Stack;

public class AssignmentStatement extends AbstractStatement {
    // must be used with the matchesString() function to correctly identify an assignment statement

    @RegExp
    private static final String assignmentRegex = "^[a-zA-Z_]+[a-zA-Z0-9_]*=.*$";
    private String id;
    private AbstractExpression expression;
    private String functionName;

    public AssignmentStatement(String id, AbstractExpression expression) {
        this.id = id;
        this.expression = expression;
        this.functionName = "main";
    }

    public AssignmentStatement(String id, AbstractExpression expression, String functionName) {
        this.id = id;
        this.expression = expression;
        this.functionName = functionName;
    }

    @Override
    public String toString() {
        return id + " = " + expression.toString();
    }

    @Override
    public ProgramState execute(@NotNull ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, SyntaxException {

        Stack<AbstractStatement> stack = programState.getExecutionStack();
        Map<String, Integer> symbols = programState.getSymbols();
        int expressionValue = expression.evaluate(symbols, programState.getHeap());

        //if the key doesn't exist, create a new one with the given value
        //if the key already exists, update the pair
        symbols.put(id, expressionValue);

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
     * @param input: String repr. of an assignment, e.g. "a=2+3" or "a=2*b+8*c"
     * @return A AssignmentStatement built from the string
     * @throws SyntaxException
     */
    public static AssignmentStatement getAssignmentStatementFromString(String input) throws SyntaxException {
        //then it has the syntax: var_name=const_value OR var_name = another_var OR var_name = arith_expr
        //remove the ';' and split by '='
        input = input.replace(";", "");
        String sides[] = input.split("=", 2);  //split by the first = only
        String varName = sides[0].replace(" ", "");      //there will always be exactly one variable in the lhs

        AbstractExpression rhsExp = ExpressionParser.getExpressionFromString(sides[1]);
        AssignmentStatement assignmentStatement = new AssignmentStatement(varName, rhsExp);
        return assignmentStatement;
    }
    /**
     * Check if the given string matches the structure of the statement described by this class
     * @param statementString string to be checked
     * @return true if the class can parse the string and output an object of this type
     *          false if the string doesn't match the class
     */
    public static boolean matchesString(String statementString) {
        return statementString.matches(assignmentRegex) && !statementString.contains(";");
    }
}
