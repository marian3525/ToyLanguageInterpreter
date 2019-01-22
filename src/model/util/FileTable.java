package model.util;

import model.adt.Pair;
import model.interfaces.FileTableInterface;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileTable implements FileTableInterface {
    //descriptor to uniquely identify one file. Will be incremented when a new file is stored
    private static int descriptor = 0;

    private Map<Integer, Pair<String, BufferedReader>> files;

    public FileTable() {
        files = new HashMap<>();
    }

    /**
     * @param filename
     * @param path
     */
    @Override
    public int storeFile(String filename, String path) throws IOException {
        descriptor++;

        //create the file if it doesn't exist
        File f = new File(path + "" + filename);
        if (!f.exists() || f.isDirectory()) {
            new BufferedWriter(new FileWriter(path + "" + filename)).close();
        }
        BufferedReader reader = new BufferedReader(new FileReader(path + "" + filename));
        Pair<String, BufferedReader> entry =  new Pair<>(filename, reader);
        files.put(descriptor, entry);
        return descriptor;
    }

    /**
     * @param descriptor: used as key to access the pair (filename, descriptor), integer
     * @return the pair (filename, jdescriptor) corresponding to descriptor
     */
    @Override
    public Pair<String, BufferedReader> getFile(int descriptor) {
        return files.get(descriptor);
    }

    /**
     * @return
     */
    @Override
    public Map<Integer, Pair<String, BufferedReader>> getAll() {
        return files;
    }

}
