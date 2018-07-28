package io;

import javafx.concurrent.Task;
import model.Complexity;
import model.Dataset;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TxtComplexityLoader extends Task<Dataset<Complexity>> implements DatasetLoader<Complexity>  {

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

        Scanner s = new Scanner(file);
        while (s.hasNextLine()){
            // todo write pattern
            String[] line = s.nextLine().split("[_;]");
            int id = Integer.parseInt(line[2]);
            int complexity = Integer.parseInt(line[3]);

            result.add(id, new Complexity(complexity));
        }
        return result;
    }

    @Override
    protected Dataset<Complexity> call() throws Exception {
        return load();
    }
}
