package model.adt;

import javafx.util.Pair;
import model.interfaces.ProcTableInterface;
import model.statement.AbstractStatement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcTable implements ProcTableInterface {
    private Map<String, Pair<List<String>, AbstractStatement>> table;

    public ProcTable() {
        table = new HashMap<>();
    }

    @Override
    public javafx.util.Pair<List<String>, AbstractStatement> get(String key) {
        return table.get(key);
    }

    @Override
    public void put(String procName, javafx.util.Pair<List<String>, AbstractStatement> value) {
        table.put(procName, value);
    }

    public Map<String, Pair<List<String>, AbstractStatement>> getAll() {
        return table;
    }

}
