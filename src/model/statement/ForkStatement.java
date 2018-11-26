package model.statement;

import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;

public class ForkStatement extends AbstractStatement {
    //.clone on the symtable to pass to the new thread
    @RegExp
    private static final String forkStatementRegex = "^fork\\(.*\\)$";
    @Override
    public String toString() {
        return null;
    }

    @Override
    public ProgramState execute(ProgramState programState) {
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
        return statementString.matches(forkStatementRegex);
    }
}
