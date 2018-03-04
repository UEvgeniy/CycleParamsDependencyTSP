package control;

import model.*;

import java.util.*;
import java.util.stream.Collectors;


public class TSPConverter {

    private TSPConverter(){}

    public static Dataset<TSPReducedMatrix> toReducedDataset(Dataset<TSPMatrix> dataset){

        Dataset<TSPReducedMatrix> result = new Dataset<>();

        for (Integer id: dataset.getKeys()){

            TSPReducedMatrix reducedMatrix = convertToReduced(dataset.getById(id), ReducingOrder.RowsColumns);
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

        /*int dimension = matrix.getDimension();
        ArrayList[] result = new ArrayList[dimension];
        Set<Integer> columnsWithZero = new HashSet<>();

        // Reduce by rows
        for (int i = 0; i < dimension; i++){

            result[i] = new ArrayList<Integer>();
            result[i].add(i == 0 ? 1 : 0);

            for (int j = 1; j < dimension; j++){
                // diagonal ignored
                if (i == j){
                    continue;
                }
                // One more minimal value
                if (matrix.get(i, j) == matrix.get(i, (int)result[i].get(0))){
                    result[i].add(j);

                }
                // New minimal value
                else if (matrix.get(i, j) < matrix.get(i, (int)result[i].get(0))){
                    result[i].clear();
                    result[i].add(j);

                }
            }
            columnsWithZero.addAll(result[i]);
        }

        List<Integer> currentColumnMinIndexes = new ArrayList<>();
        for (int j = 0; j < dimension; j++){
            if (columnsWithZero.contains(j)){
                continue;
            }
            currentColumnMinIndexes.clear();
            currentColumnMinIndexes.add( j == 0 ? 1 : 0);

            for (int i = 0; i < dimension; i++){
                // diagonal ignored
                if (i == j){
                    continue;
                }
                // One more minimal value
                if (matrix.get(i, j) == matrix.get(i, currentColumnMinIndexes.get(0))){
                    currentColumnMinIndexes.add(i);
                }
                // New minimal value
                else if (matrix.get(i, j) < matrix.get(i, currentColumnMinIndexes.get(0))){
                    currentColumnMinIndexes.clear();
                    currentColumnMinIndexes.add(j);
                }
            }
            // add extra zeros in result
            for (Integer i : currentColumnMinIndexes){
                result[i].add(j);
            }
        }
        return result;*/
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

    /*private void reduce(TSPMatrix matrix, boolean rowsFirst){
        if (rowsFirst){
            for (int i = 0; i < matrix.getDimension(); i++){
                for (int j = 0; i < matrix.getDimension(); i++){
                    foo(i, j);
                }
            }
        }
        else{
            for (int i = 0; i < matrix.getDimension(); i++){
                for (int j = 0; i < matrix.getDimension(); i++){
                    foo(j, i);
                }
            }
        }

    }

    private void foo(Integer i, Integer j, TSPMatrix matrix){
        // diagonal ignored
        if (i == j){
            continue;
        }
        // One more minimal value
        if (matrix.get(i, j) == matrix.get(i, (int)result[i].get(0))){
            result[i].add(j);

        }
        // New minimal value
        else if (matrix.get(i, j) < matrix.get(i, (int)result[i].get(0))){
            result[i].clear();
            result[i].add(j);
    }*/

    public static List<Double> toParamsDataset(List<TSPReducedMatrix> dataset,
                                               ReducedMatrixParameter param){
        return dataset.stream().map(param).collect(Collectors.toList());
    }

    public static List<Double> toDouble(List<Complexity> list){
        return list.stream().map((c) -> (double)c.get()).collect(Collectors.toList());
    }
}
