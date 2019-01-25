package repository;

import exceptions.RepositoryException;
import javafx.util.Pair;
import model.programState.ProgramState;
import model.statement.AbstractStatement;

import java.util.List;
import java.util.Map;

public interface RepositoryInterface {
    void addProgram(String progName, ProgramState programState) throws RepositoryException;

    void addProcedure(String name, Pair<List<String>, AbstractStatement> proc, String progName);

    ProgramState getProgramByName(String progName) throws RepositoryException;

    void logProgramState(ProgramState state);

    Map<String, ProgramState> getPrograms();

    void setPrograms(Map<String, ProgramState> newPrograms);

    Map<String, String> getStrings(ProgramState state);

    void setPath(String path);
}