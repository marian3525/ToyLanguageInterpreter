package repository;

import exceptions.RepositoryException;
import model.programState.ProgramState;

import java.util.Map;

public interface RepositoryInterface {
    void addProgram(String progName, ProgramState programState) throws RepositoryException;

    ProgramState getProgramByName(String progName) throws RepositoryException;

    void logProgramState(ProgramState state);

    Map<String, ProgramState> getPrograms();

    void setPrograms(Map<String, ProgramState> newPrograms);

    Map<String, String> getStrings(ProgramState state);

    void setPath(String path);
}