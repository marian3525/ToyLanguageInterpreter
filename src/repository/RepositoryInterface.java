package repository;

import exceptions.RepositoryException;
import model.programState.ProgramState;

public interface RepositoryInterface {
    void addProgram(String progName, ProgramState programState) throws RepositoryException;

    ProgramState getProgramByName(String progName) throws RepositoryException;
}