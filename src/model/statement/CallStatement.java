package model.statement;

import exceptions.SyntaxException;
import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import javafx.util.Pair;
import model.expression.AbstractExpression;
import model.interfaces.ProcTableInterface;
import model.programState.ProgramState;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CallStatement extends AbstractStatement {

    private String fname;
    private List<AbstractExpression> args;

    public CallStatement(String fname, List<AbstractExpression> args) {
        this.fname = fname;
        this.args = args;
    }

    @Override
    public String toString() {
        return "call " + fname + "(" + args + ")";
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException, SyntaxException {

        ProcTableInterface procs = programState.getProcTable();
        Pair<List<String>, AbstractStatement> procedure = procs.get(fname);
        if(procedure == null) {
            throw new UndefinedOperationException("Procedure " + fname + " not defined");
        }
        List<String> formals = procedure.getKey();
        AbstractStatement body = procedure.getValue();

        HashMap<String, Integer> procSymbols = new HashMap<>();

        // map the formal param to their actual values
        for(int idx=0; idx < args.size(); idx++) {
            procSymbols.put(formals.get(idx), args.get(idx).evaluate(programState.getSymbols(), programState.getHeap()));
        }

        // push the new symbols onto the symbols stack
        programState.getSymbolsStack().push(procSymbols);

        // push the return statement
        programState.getExecutionStack().push(new ReturnStatement());

        // push the body of the function on the top of the execution stack
        programState.getExecutionStack().push(body);

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
        return false;//statementString.matches(callStatementRegex);
    }
}
