package io;

import model.Dataset;
import model.TSPReducedMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Objects;

public class ObjReducedDatasetSaver {

    private Dataset<TSPReducedMatrix> reducedMatrixDataset;
    private static String EXTENSION = "redmat";
    private File file;

    public ObjReducedDatasetSaver(Dataset<TSPReducedMatrix> dataset, File file){
        reducedMatrixDataset = Objects.requireNonNull(dataset);
        this.file = Objects.requireNonNull(file);
    }


    public void save() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(file));
        oos.writeObject(reducedMatrixDataset);
        oos.close();
    }

}
