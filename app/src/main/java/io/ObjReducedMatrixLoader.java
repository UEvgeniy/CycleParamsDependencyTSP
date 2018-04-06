package io;

import model.Dataset;
import model.TSPReducedMatrix;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjReducedMatrixLoader implements DatasetLoader<TSPReducedMatrix> {

    private List<File> files;
    private static String EXTENSION = "redmat";

    public ObjReducedMatrixLoader(File file) {
        this(file, false);
    }

    public ObjReducedMatrixLoader(File file, boolean withSubfolders){
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

        for (File f : files){

            try {
                readAndAdd(result, f);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return result;
            }
        }
        return result;
    }

    private void readAndAdd(Dataset<TSPReducedMatrix> dataset, File file) throws IOException, ClassNotFoundException {

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));

        TSPReducedMatrix reduced = (TSPReducedMatrix)ois.readObject();
        dataset.add(Integer.valueOf(file.getName().replaceFirst("[.][^.]+$", "")), reduced);
    }
}
