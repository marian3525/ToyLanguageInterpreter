package model.statement;

import model.programState.ProgramState;
import org.intellij.lang.annotations.RegExp;

public class ReturnStatement extends AbstractStatement {

    @RegExp
    private static final String returnStatementRegex = "^return .*$";


    @Override
    public String toString() {
        return "return()";
    }

    @Override
    public ProgramState execute(ProgramState programState) {

        // restore the caller's symbols
        programState.getSymbolsStack().pop();
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
        return statementString.matches(returnStatementRegex);
    }
}
