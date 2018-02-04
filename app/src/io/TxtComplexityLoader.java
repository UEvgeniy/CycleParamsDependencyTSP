package io;

import model.Complexity;
import model.Dataset;

import java.io.File;
import java.io.FileNotFoundException;

public class TxtComplexityLoader implements DatasetLoader<Complexity> {

    private File file;
    private static String EXTENSION = "txt";

    public TxtComplexityLoader(File file) {
        if (!hasExtension(file, EXTENSION)) {
            String EXC = "Complexity loader must have " + EXTENSION + " extension file";
            throw new IllegalArgumentException(EXC);
        }

        this.file = file;
    }

    @Override
    public Dataset<Complexity> load() throws FileNotFoundException{
        Dataset<Complexity> result = new Dataset<>();
        // todo write complexity loader
        return result;
    }
}
