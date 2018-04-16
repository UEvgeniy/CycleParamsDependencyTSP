package io;

import control.TSPConverter;
import javafx.concurrent.Task;
import model.Dataset;
import model.TSPMatrix;
import model.TSPReducedMatrix;

import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;


public class TxtMatrixLoader extends Task<Dataset<TSPReducedMatrix>> implements DatasetLoader<TSPMatrix> {

    private List<File> files;
    private static String EXTENSION = "txt";

    public TxtMatrixLoader(File file) {
        this(file, false);
    }

    public TxtMatrixLoader(File file, boolean withSubfolders){
        if (withSubfolders) {
            files = getWithSubfolders(file, EXTENSION);
        }
        else{
            files = new ArrayList<>();
            for (File f : Objects.requireNonNull(file.listFiles())){
                if (hasExtension(f, EXTENSION)){
                    files.add(f);
                }
            }

        }
    }

    @Override
    public Dataset<TSPMatrix> load() throws FileNotFoundException {
        Dataset<TSPMatrix> result = new Dataset<>();

        int count = 0;
        for (File f : files){
            readAndAdd(result, f);
            this.updateProgress(++count, files.size());
        }
        this.updateProgress(1, 1);
        return result;
    }

    private void readAndAdd(Dataset<TSPMatrix> dataset, File file) throws FileNotFoundException{

        // todo replace to pattern
        String[] name = file.getName().split("_");
        int id = Integer.parseInt(name[2].substring(0, name[2].indexOf('.')));
        int size = Integer.parseInt(name[1]);

        Scanner s = new Scanner(file);
        TSPMatrix matrix = new TSPMatrix(size);

        int element = 0;

        while (s.hasNext()){
            matrix.set((element / size), (element++ % size), s.nextInt());
        }

        if (element != (size * size)){
            throw new IllegalArgumentException("File " + name + " has " + element +
                    " elements in array instead of " + (size * size));
        }

        dataset.add(id, matrix);
    }

    @Override
    protected Dataset<TSPReducedMatrix> call() throws Exception {
        return null;
    }
}
