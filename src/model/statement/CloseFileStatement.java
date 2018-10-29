package model.statement;

import exceptions.UndefinedOperationException;
import exceptions.UndefinedVariableException;
import model.expression.Expression;
import model.programState.ProgramState;

import java.io.IOException;

public class CloseFileStatement extends Statement {

    private Expression fileId;

    public CloseFileStatement(Expression fileId) {
        this.fileId = fileId;
    }

    @Override
    public String toString() {
        return "Close file: " + fileId;
    }

    @Override
    public ProgramState execute(ProgramState programState) throws UndefinedOperationException, UndefinedVariableException, IOException {
        int descriptor = fileId.evaluate(programState.getSymbols());
        //close the file
        programState.getFiles().getFile(descriptor).getValue().close();
        //and delete the entry from the table
        programState.getFiles().getAll().remove(descriptor);

        return programState;
    }
}
