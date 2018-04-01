package io;

import com.sun.istack.internal.NotNull;
import model.Dataset;
import model.TSPReducedMatrix;

import java.io.*;
import java.util.Objects;

public class ObjReducedMatrixSaver {

    private Dataset<TSPReducedMatrix> reducedMatrixDataset;
    private static String EXTENSION = "redmat";
    private File file;

    public ObjReducedMatrixSaver(Dataset<TSPReducedMatrix> dataset, File file){
        reducedMatrixDataset = Objects.requireNonNull(dataset);
        this.file = Objects.requireNonNull(file);
    }


    public void save() throws IOException {

        for (Integer key: reducedMatrixDataset.getKeys()){

            String name = file.getAbsolutePath() + "//" +
                    key +
                    "." +
                    EXTENSION;

            File dir = new File(file.getAbsolutePath());
            dir.mkdirs();

            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(new File(name)));
            oos.writeObject(reducedMatrixDataset.getById(key));
            oos.close();
        }
    }

}
