package control;


import model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TSPConverter {

    private TSPConverter(){}

    public static Dataset<TSPReducedMatrix> toReducedDataset(Dataset<TSPMatrix> dataset){

        Dataset<TSPReducedMatrix> result = new Dataset<>();

        for (Integer id: dataset.getKeys()){
            TSPReducedMatrix reducedMatrix = new TSPReducedMatrix(
                    getMinRoutes(dataset.getById(id))
            );
            result.add(id, reducedMatrix);
        }
        return result;
    }

    private static int[] getMinRoutes(TSPMatrix matrix){
        int dimension = matrix.getDimension();
        int[] result = new int[dimension];

        for (int i = 0; i < dimension; i++){

            int minIndex = (i == 0) ? 1 : 0;
            for (int j = 1; j < dimension; j++){

                if (matrix.get(i, j) < matrix.get(i, minIndex) && i != j){
                    minIndex = j;
                }
            }
            result[i] = minIndex;
        }
        return result;
    }

    public static List<Double> toParamsDataset(List<TSPReducedMatrix> dataset,
                                               ReducedMatrixParameter param){
        return dataset.stream().map(param).collect(Collectors.toList());
    }

    public static List<Double> toDouble(List<Complexity> list){
        return list.stream().map((c) -> (double)c.get()).collect(Collectors.toList());
    }
}
