package model.interfaces;

import javafx.util.Pair;
import model.statement.AbstractStatement;

import java.util.List;
import java.util.Map;

public interface ProcTableInterface {

    Pair<List<String>, AbstractStatement> get(String key);
    void put(String procName, Pair<List<String>, AbstractStatement> value);
    Map<String, Pair<List<String>, AbstractStatement>> getAll();
}
