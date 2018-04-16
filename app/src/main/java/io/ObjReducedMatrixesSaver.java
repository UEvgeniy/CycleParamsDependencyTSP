package io;


import model.Dataset;
import model.TSPReducedMatrix;

import java.io.*;
import java.util.Objects;

public class ObjReducedMatrixesSaver {

    private Dataset<TSPReducedMatrix> reducedMatrixDataset;
    private static String EXTENSION = "rm";
    private File file;

    public ObjReducedMatrixesSaver(Dataset<TSPReducedMatrix> dataset, File file){
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
