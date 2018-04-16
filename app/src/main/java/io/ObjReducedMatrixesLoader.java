package io;

import javafx.concurrent.Task;
import model.Dataset;
import model.TSPReducedMatrix;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjReducedMatrixesLoader extends Task<Dataset<TSPReducedMatrix>>
        implements DatasetLoader<TSPReducedMatrix> {

    private List<File> files;
    private static String EXTENSION = "rm";

    public ObjReducedMatrixesLoader(File file) {
        this(file, false);
    }

    public ObjReducedMatrixesLoader(File file, boolean withSubfolders){
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
    public Dataset<TSPReducedMatrix> load() throws IOException {
        Dataset<TSPReducedMatrix> result = new Dataset<>();

        int count = 0;
        for (File f : files){

            try {
                readAndAdd(result, f);
                this.updateProgress(++count, files.size());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return result;
            }
        }
        this.updateProgress(1, 1);
        return result;
    }

    private void readAndAdd(Dataset<TSPReducedMatrix> dataset, File file) throws IOException, ClassNotFoundException {

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));

        TSPReducedMatrix reduced = (TSPReducedMatrix)ois.readObject();
        dataset.add(Integer.valueOf(file.getName().replaceFirst("[.][^.]+$", "")), reduced);
    }

    @Override
    protected Dataset<TSPReducedMatrix> call() throws Exception {
        return null;
    }
}
