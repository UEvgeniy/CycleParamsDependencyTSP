package control;


import model.Dataset;
import model.TSPMatrix;
import model.TSPReducedMatrix;

public class MatrixConverter {

    private MatrixConverter(){}

    public static Dataset<TSPReducedMatrix> convertToReducedDataset(Dataset<TSPMatrix> dataset){

        Dataset<TSPReducedMatrix> result = new Dataset<>();


        // todo convert

        return result;
    }
}
