package model.adt;

import javafx.util.Pair;
import model.interfaces.FileTableInterface;

import java.io.*;
import java.util.Map;

public class FileTable implements FileTableInterface {
    //descriptor to uniquely identify one file. Will be incremented when a new file is stored
    private static int descriptor = 0;

    private Map<Integer, Pair<String, BufferedReader>> files;

    public FileTable() {
        files = new HashMap<Integer, Pair<String, BufferedReader>>();
    }

    /**
     * @param filename
     * @param path
     */
    @Override
    public int storeFile(String filename, String path) throws IOException {
        descriptor++;
        //create the file if it doesn't exist
        File f = new File(path + "\\" + filename);
        if (!f.exists() || f.isDirectory()) {
            new BufferedWriter(new FileWriter(path + "\\" + filename)).close();
        }
        files.put(descriptor, new Pair<>(filename, new BufferedReader(
                new FileReader(path + "\\" + filename))));
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

    public int getUID() {

        return descriptor;
    }

}
