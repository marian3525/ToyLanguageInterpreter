package repository;

import exceptions.ProgramException;
import exceptions.RepositoryException;
import model.programState.ProgramState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Repository {
    //vector of running programs
    private Map<String, ProgramState> progs;
    private static boolean isCreated = false;

    private Repository() {
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

    public ProgramState getProgramByName(String progName) throws ProgramException {
        if(progs.containsKey(progName)) {
            return progs.get(progName);
        }
        else {
            throw new ProgramException("Program with name: " + progName + " does not exist");
        }
    }

    @Nullable
    public static Repository makeRepository() {
        if(isCreated) {
            return null;
        }
        else {
            isCreated = true;
            return new Repository();
        }
    }
}
