package repository;

import exceptions.RepositoryException;
import model.programState.ProgramState;

import java.io.IOException;

public interface RepositoryInterface {
    void addProgram(String progName, ProgramState programState) throws RepositoryException;

    ProgramState getProgramByName(String progName) throws RepositoryException;

    void logProgramState(ProgramState state) throws IOException;

    void setPath(String path);
}