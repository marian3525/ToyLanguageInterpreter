package model.statement;

import exceptions.UndefinedVariableException;
import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;

public class DecrementStatement extends AbstractStatement {
    @RegExp
    private static final String decrementStatementRegex = "^([a-zA-Z_]+[a-zA-Z0-9_]*)--$";
    private String varName;

    public DecrementStatement(String varName) {
        this.varName = varName;
    }

    public static DecrementStatement getDecrementStatementFromString(String statement) {
        String varName = statement.replace(" ", "").replace("--", "");
        return new DecrementStatement(varName);
    }

    @Override
    public String toString() {
        return varName + "--";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedVariableException {
        Integer value = programState.getSymbols().get(varName);
        if (value == null) {
            // var name not in table, throw
            throw new UndefinedVariableException("Variable " + varName + " not defined");
        }
        value = value - 1;
        programState.getSymbols().put(varName, value);
        return null;
    }

    @Override
    public String getFunction() {
        return null;
    }

    @Override
    public void setFunction(String functionName) {

    }
    /**
     * Check if the given string matches the structure of the statement described by this class
     * @param statementString string to be checked
     * @return true if the class can parse the string and output an object of this type
     *          false if the string doesn't match the class
     */
    public static boolean matchesString(String statementString) {
        return statementString.matches(decrementStatementRegex);
    }
}
