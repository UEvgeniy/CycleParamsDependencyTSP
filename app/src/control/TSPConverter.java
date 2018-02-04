package control;


import model.*;

import java.util.ArrayList;
import java.util.List;
public class TSPConverter {

    private TSPConverter(){}

    public static Dataset<TSPReducedMatrix> toReducedDataset(Dataset<TSPMatrix> dataset){

        Dataset<TSPReducedMatrix> result = new Dataset<>();
        // todo convert

        return result;
    }

    public static List<Double> toParamsDataset(List<TSPReducedMatrix> dataset,
                                               ReducedMatrixParameter param){
        List<Double> result = new ArrayList<>();
        // todo convert
        return result;
    }

    public static List<Double> toDouble(List<Complexity> list){
        List<Double> result = new ArrayList<>();
        // todo convert
        return result;
    }
}
