package io;

import model.Dataset;
import model.TSPMatrix;

import java.io.File;
import java.util.List;


public class TxtMatrixLoader implements DatasetLoader<TSPMatrix> {

    private List<File> files;
    private static String EXTENSION = "txt";

    public TxtMatrixLoader(File file) {
        this(file, false);
    }

    public TxtMatrixLoader(File file, boolean withSubfolders){
      files = getWithSubfolders(file, EXTENSION);
    }

    @Override
    public Dataset<TSPMatrix> load() {
        Dataset<TSPMatrix> result = new Dataset<>();
        // todo load matrixes
        return result;
    }
}
