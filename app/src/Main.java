import control.*;
import model.BindedData;
import io.TxtComplexityLoader;
import io.TxtMatrixLoader;
import model.*;
import util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;

public class Main {

    public static void main (String[] args) {
        try {
            printDatasets(args[0], args[1], System.out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private static Dataset<TSPMatrix> loadTSPs(String path, boolean subfolders) throws FileNotFoundException {
        File file = new File(path);
        TxtMatrixLoader loader = new TxtMatrixLoader(file, subfolders);

        return loader.load();
    }

    private static Dataset<Complexity> loadComplexities(String path) throws FileNotFoundException {
        File file = new File(path);
        TxtComplexityLoader loader = new TxtComplexityLoader(file);

        return loader.load();

    }

    private static BindedData<TSPReducedMatrix, Complexity> bindDatasets(
            Dataset<TSPReducedMatrix> dRedused, Dataset<Complexity> dComplexity){

        DataBinder<TSPReducedMatrix, Complexity> binder = new DataBinder<>(dRedused, dComplexity);
        return binder.bind();
    }

    private static void printDatasets(String pathTSP, String pathComplexity, PrintStream ps) throws FileNotFoundException {

        Dataset<TSPReducedMatrix> dReduced = TSPConverter
                .toReducedDataset(loadTSPs(pathTSP, true), ReducingOrder.RowsColumns);

        BindedData<TSPReducedMatrix, Complexity> data =
                bindDatasets(dReduced, loadComplexities(pathComplexity));


        for (int i = 0; i < data.getFirst().size(); i++){
            ps.print(i+1 + ". ");
            ps.print("Compl: " + data.getSecond().get(i).get());

            List<Integer> cycles = Parameters.getCycles(data.getFirst().get(i));
            ps.print(". Size: "+ cycles.size() + ": ");

            Map<Integer, Integer> map = new HashMap<>();

            for (Integer j : cycles){
                if (map.containsKey(j)){
                    map.put(j, map.get(j) + 1);
                }
                else{
                    map.put(j, 1);
                }
            }

            map = Utils.sortByValue(map, Utils.Compare::byKeys);

            for (Integer key: map.keySet()){
                ps.print("\'" + key + "\'-" + map.get(key) + "шт; ");
            }
            ps.println();
        }

    }






    /////////////////////////////////////////////////
    private static void example(String[] args){
        // Init location of data files
        File matrFile = new File(args[0]);
        File complFile = new File(args[1]);

        // Init data loaders
        TxtMatrixLoader mLoader = new TxtMatrixLoader(matrFile, true);
        TxtComplexityLoader cLoader = new TxtComplexityLoader(complFile);

        // Load data to datasets
        Dataset<TSPMatrix> datasetMatr;
        Dataset<Complexity> datasetCompl;
        try {
            datasetMatr = mLoader.load();
            datasetCompl = cLoader.load();

            // Convert TSP Matrixes to reduced ones
            Dataset<TSPReducedMatrix> reduced = TSPConverter.toReducedDataset(datasetMatr, ReducingOrder.RowsColumns);

            // Bind reduced matrixes and its complexity by ids
            DataBinder<TSPReducedMatrix, Complexity> binder = new DataBinder<>(reduced, datasetCompl);
            BindedData<TSPReducedMatrix, Complexity> lists = binder.bind();

            // Select reduced matrix parameter
            ReducedMatrixParameter param = Parameters::maxCycleLength;

            // Select correlation coefficient
            Correlation correlation = new PearsonCorrelation(); // or new SpearmanCorrelation();

            // Count correlation between Reduced Matrix Param and Complexity
            double result = correlation.count(
                    TSPConverter.toParamsDataset(lists.getFirst(), param),
                    TSPConverter.toDouble(lists.getSecond())
            );

            System.out.println("The result correlation is " + result);
        }
        catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    private static void print(String[] args){
        // Init location of data files
        File matrFile = new File(args[0]);
        File complFile = new File(args[1]);

        // Init data loaders
        TxtMatrixLoader mLoader = new TxtMatrixLoader(matrFile, true);
        TxtComplexityLoader cLoader = new TxtComplexityLoader(complFile);

        // Load data to datasets
        Dataset<TSPMatrix> datasetMatr;
        Dataset<Complexity> datasetCompl;
        try {
            datasetMatr = mLoader.load();
            datasetCompl = cLoader.load();

            // Convert TSP Matrixes to reduced ones
            Dataset<TSPReducedMatrix> reduced = TSPConverter.toReducedDataset(datasetMatr, ReducingOrder.RowsColumns);

            // Bind reduced matrixes and its complexity by ids
            DataBinder<TSPReducedMatrix, Complexity> binder = new DataBinder<>(reduced, datasetCompl);
            BindedData<TSPReducedMatrix, Complexity> lists = binder.bind();

            for (int i = 0; i < lists.getFirst().size(); i++){
                System.out.print(i+1 + ". ");
                System.out.print("Compl: " + lists.getSecond().get(i).get());

                List<Integer> cycles = Parameters.getCycles(lists.getFirst().get(i));
                System.out.print(". Size: "+ cycles.size() + ": ");

                Map<Integer, Integer> map = new HashMap<>();

                for (Integer j : cycles){
                    if (map.containsKey(j)){
                        map.put(j, map.get(j) + 1);
                    }
                    else{
                        map.put(j, 1);
                    }
                }

                map = Utils.sortByValue(map, Utils.Compare::byValues);

                for (Integer key: map.keySet()){
                    System.out.print("\'" + key + "\'-" + map.get(key) + "шт; ");
                }
                System.out.println();
            }
        }
        catch (Exception e){
            System.out.print(e.getMessage());
        }
    }


}
