package repository;

import exceptions.RepositoryException;
import model.programState.ProgramState;

import java.util.HashMap;
import java.util.Map;

public class Repository implements RepositoryInterface {
    //vector of running programs
    private Map<String, ProgramState> progs;
    private static boolean isCreated = false;

    public Repository() {
        progs = new HashMap<>();
    }

    public void addProgram(String progName, ProgramState programState) throws RepositoryException {
        if(progs.containsKey(progName)) {
            throw new RepositoryException("Program " + progName + " already exists!");
        }
        else {
            progs.put(progName, programState);
        }
    }

    public ProgramState getProgramByName(String progName) throws RepositoryException {
        if(progs.containsKey(progName)) {
            return progs.get(progName);
        }
        else {
            throw new RepositoryException("Program with name: " + progName + " does not exist");
        }
    }
}
