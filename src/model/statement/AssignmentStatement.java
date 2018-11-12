package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.AbstractExpression;
import model.expression.ArithmeticExpression;
import model.expression.ConstantExpression;
import model.expression.VariableExpression;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import static model.expression.AbstractExpression.getExpressionType;


public class AssignmentStatement extends AbstractStatement {
    private String id;
    private AbstractExpression expression;
    private String functionName;
    @RegExp
    public static final String assignmentRegex= "^[a-zA-Z_]+[a-zA-Z0-9_]*=[+-]?([0]{1}$|[1-9][0-9]*$)";

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

    /**
     * @param input: String repr. of an assignment, e.g. "a=2+3" or "a=2*b+8*c"
     * @return A AssignmentStatement built from the string
     * @throws SyntaxException
     */
    public static AssignmentStatement getAssignmentStatementFromString(String input) throws SyntaxException {
        //then it has the syntax: var_name=const_value OR var_name = another_var OR var_name = arith_expr
        //remove the ';' and split by '='
        input = input.replace(";", "");
        String sides[] = input.split("=");
        String varName = sides[0];      //there will always be exactly one variable in the lhs
        AssignmentStatement s = null;
        //depending on whether the rhs is a const, var or arith_expr

        switch (getExpressionType(sides[1])) {
            case "ConstantExpression":
                int value = Integer.parseInt(sides[1]);
                s = new AssignmentStatement(varName, new ConstantExpression(value));
                break;

            case "VariableExpression":
                String rhsVarName = sides[1];
                s = new AssignmentStatement(varName, new VariableExpression(rhsVarName));
                break;

            case "ArithmeticExpression":
                String rhsExpr = sides[1];
                ArithmeticExpression arithmeticExpression;
                Vector<String> postfix = AbstractExpression.infixToPostfix(rhsExpr);
                arithmeticExpression = AbstractExpression.buildExpressionFromPostfix(postfix);
                s = new AssignmentStatement(varName, arithmeticExpression);
                break;
        }
        return s;
    }

    @Override
    public String toString() {
        return id + " = " + expression.toString();
    }

    @Override
    public ProgramState execute(@NotNull ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException {

        Stack<AbstractStatement> stack = programState.getExecutionStack();
        Map<String, Integer> symbols = programState.getSymbols();
        int expressionValue = expression.evaluate(symbols, programState.getHeap());

        //if the key doesn't exist, create a new one with the given value
        //if the key already exists, update the pair
        symbols.put(id, expressionValue);

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
