package model.interfaces;

import model.adt.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public interface FileTableInterface {
    int storeFile(String filename, String path) throws IOException;

    Pair<String, BufferedReader> getFile(int descriptor) throws IOException;

    Map<Integer, Pair<String, BufferedReader>> getAll();
}
