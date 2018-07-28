package control;

import control.functionals.ReducedMatrixFunctional;
import model.*;

import java.util.*;
import java.util.stream.Collectors;


public class TSPConverter {

    private TSPConverter(){}

    public static Dataset<TSPReducedMatrix> toReducedDataset(Dataset<TSPMatrix> dataset, ReducingOrder order){

        Dataset<TSPReducedMatrix> result = new Dataset<>();

        for (Integer id: dataset.getKeys()){

            TSPReducedMatrix reducedMatrix = convertToReduced(dataset.getById(id), order);
            result.add(id, reducedMatrix);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static TSPReducedMatrix convertToReduced(TSPMatrix matrix, ReducingOrder order){

        if (order == ReducingOrder.RowsColumns){
            reduceByRows(matrix);
            reduceByColumns(matrix);
        }
        else{
            reduceByColumns(matrix);
            reduceByRows(matrix);
        }

        ArrayList[] zeros = new ArrayList[matrix.getDimension()];
        for (int i = 0; i < matrix.getDimension(); i++){
            zeros[i] = new ArrayList<Integer>();
            for (int j = 0; j < matrix.getDimension(); j++){
                if (matrix.get(i, j) == 0 && i != j){
                    zeros[i].add(j);
                }
            }
        }

        return new TSPReducedMatrix(zeros);
    }


    private static void reduceByRows(TSPMatrix matrix){
        for (int i = 0; i < matrix.getDimension(); i++){

            int minRowVal = matrix.get(i, i == 0 ? 1 : 0);

            // Find min value in row (not equal zero)
            for (int j = 0; j < matrix.getDimension(); j++){
                // Main diagonal ignored
                if (i == j){
                    continue;
                }

                // The row is already reduced
                if (matrix.get(i, j) == 0){
                    minRowVal = 0;
                    break;
                }

                // New min val detected
                if (matrix.get(i, j) < minRowVal){
                    minRowVal = matrix.get(i, j);
                }
            }

            // Reduce
            if (minRowVal != 0){
                for (int j = 0; j < matrix.getDimension(); j++){
                    if (i != j){
                        matrix.set(i, j, matrix.get(i, j) - minRowVal);
                    }
                }
            }
        }
    }

    private static void reduceByColumns(TSPMatrix matrix){
        for (int j = 0; j < matrix.getDimension(); j++){

            int minColVal = matrix.get(j == 0 ? 1 : 0, j);

            // Find min value in row (not equal zero)
            for (int i = 0; i < matrix.getDimension(); i++){
                // Main diagonal ignored
                if (i == j){
                    continue;
                }

                // The row is already reduced
                if (matrix.get(i, j) == 0){
                    minColVal = 0;
                    break;
                }

                // New min val detected
                if (matrix.get(i, j) < minColVal){
                    minColVal = matrix.get(i, j);
                }
            }

            // Reduce
            if (minColVal != 0){
                for (int i = 0; i < matrix.getDimension(); i++){
                    if (i != j){
                        matrix.set(i, j, matrix.get(i, j) - minColVal);
                    }
                }
            }
        }
    }

    public static List<Double> toParamsDataset(List<TSPReducedMatrix> dataset,
                                               ReducedMatrixFunctional param){
        return dataset.stream().map(param).collect(Collectors.toList());
    }

    public static List<Double> toDouble(List<Complexity> list){
        return list.stream().map((c) -> (double)c.get()).collect(Collectors.toList());
    }
}
