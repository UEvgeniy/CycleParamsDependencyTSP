package io;

import model.Dataset;
import model.TSPReducedMatrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObjWholeLoader implements DatasetLoader<TSPReducedMatrix> {

    private File file;
    private static String EXTENSION = "redmat";

    public ObjWholeLoader(File file) {
        this(file, false);
    }

    public ObjWholeLoader(File file, boolean withSubfolders){
        this.file = file;
    }

    @Override
    public Dataset<TSPReducedMatrix> load() throws IOException, ClassNotFoundException {

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));

        return  (Dataset<TSPReducedMatrix>)ois.readObject();


    }

    private void readAndAdd(Dataset<TSPReducedMatrix> dataset, File file) throws IOException, ClassNotFoundException {

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));

        TSPReducedMatrix reduced = (TSPReducedMatrix)ois.readObject();
        dataset.add(Integer.valueOf(file.getName().replaceFirst("[.][^.]+$", "")), reduced);
    }
}
