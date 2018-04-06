import control.*;
import io.ObjReducedMatrixesLoader;
import io.ObjReducedMatrixesSaver;
import io.TxtComplexityLoader;
import io.TxtMatrixLoader;
import model.*;
import util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UseCases {

    public static Dataset<TSPMatrix> loadTSPs(String path, boolean subfolders) throws FileNotFoundException {
        File file = new File(path);
        TxtMatrixLoader loader = new TxtMatrixLoader(file, subfolders);

        return loader.load();
    }

    public static Dataset<Complexity> loadComplexities(String path) throws FileNotFoundException {
        File file = new File(path);
        TxtComplexityLoader loader = new TxtComplexityLoader(file);

        return loader.load();

    }

    public static BindedData<TSPReducedMatrix, Complexity>
    bindDatasets(Dataset<TSPReducedMatrix> dRedused, Dataset<Complexity> dComplexity){

        DataBinder<TSPReducedMatrix, Complexity> binder = new DataBinder<>(dRedused, dComplexity);
        return binder.bind();
    }

    public static void serializeReducedTSP(Dataset<TSPReducedMatrix> dataset, String path) throws IOException {

        ObjReducedMatrixesSaver orms = new ObjReducedMatrixesSaver(dataset, new File(path));
        orms.save();
    }

    public static Dataset<TSPReducedMatrix> deserializeReducedTSP(String path, boolean withSubfolders) throws IOException {
        ObjReducedMatrixesLoader orml = new ObjReducedMatrixesLoader(new File(path), withSubfolders);
        return orml.load();

    }

    public static Dataset<TSPReducedMatrix> loadTxtTSPAndConvert(
            String path, boolean withSubfolders, ReducingOrder order) throws FileNotFoundException {

        Dataset<TSPMatrix> matrixDataset = loadTSPs(path, withSubfolders);
        return TSPConverter.toReducedDataset(matrixDataset, order);
    }


    public static double experiment(
             Dataset<TSPReducedMatrix> dReduced,
             Dataset<Complexity> dComplexities,
             ReducedMatrixParameter param,
             Correlation correlation){

        BindedData<TSPReducedMatrix, Complexity> binded = bindDatasets(dReduced, dComplexities); // bind by IDs

        List<Double> paramsOfReducedMatrix = TSPConverter.toParamsDataset(binded.getFirst(), param);
        List<Double> lComplexities = TSPConverter.toDouble(binded.getSecond());

        return correlation.count(paramsOfReducedMatrix, lComplexities);


        /*
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
        */
    }

    public static void printData(BindedData<TSPReducedMatrix, Complexity> data, PrintStream ps) {

        for (int i = 0; i < data.getFirst().size(); i++){
            ps.print(i+1 + ". ");
            ps.print("Compl: " + data.getSecond().get(i).get());


            Map<Integer, Integer> map = countCycles(data.getFirst().get(i));

            ps.print(". Size: "+ map.values().stream().mapToInt(Integer::intValue).sum() + ": ");

            map = Utils.sort(map, Utils.Compare::byKeys);

            for (Integer key: map.keySet()){
                ps.print("\'" + key + "\'-" + map.get(key) + "шт; ");
            }
            ps.println();
        }

    }


    private static class Binded implements Comparable<Binded>{
        TSPReducedMatrix m;
        Integer i;
        private Binded(TSPReducedMatrix m, Complexity c){
            this.m = m;
            this.i = c.get();
        }

        @Override
        public int compareTo(Binded o) {
            return i - o.i;
        }
    }

    public static void table(BindedData<TSPReducedMatrix, Complexity> data, PrintStream ps, String sep){

        List<TSPReducedMatrix> reduced = data.getFirst();
        List<Complexity> comp = data.getSecond();

        List<Binded> sorted = IntStream.range(0, reduced.size())
                .mapToObj(i -> new Binded(reduced.get(i), comp.get(i))) // Create the instance
                .sorted(Comparator.comparingInt(b -> b.i))              // Sort using a Comparator
                .collect(Collectors.toList());


        for (Binded b : sorted){

            ps.print(b.i);
            ps.print(sep);

            Map<Integer, Integer> map = countCycles(b.m);
            map = Utils.sort(map, Utils.Compare::byKeys);


            for (int key = 1; key < b.m.getMinRoutes().length; key++){ // todo change
                ps.print(map.get(key) == null ? 0 : map.get(key));
                ps.print(sep);
            }
            ps.println();
        }
    }

    private static Map<Integer, Integer> countCycles(TSPReducedMatrix rm){
        Map<Integer, Integer> map = new HashMap<>();
        List<Integer> cycles = Parameters.getCycles(rm);

        for (Integer j : cycles){
            if (map.containsKey(j)){
                map.put(j, map.get(j) + 1);
            }
            else{
                map.put(j, 1);
            }
        }
        return map;
    }
}
