import control.*;
import control.functionals.ReducedMatrixFunctional;
import io.ObjReducedMatrixesLoader;
import io.ObjReducedMatrixesSaver;
import io.TxtComplexityLoader;
import io.TxtMatrixLoader;
import model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

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
    bindDatasets(Dataset<TSPReducedMatrix> dReduced, Dataset<Complexity> dComplexity){

        DataBinder<TSPReducedMatrix, Complexity> binder = new DataBinder<>(dReduced, dComplexity);
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
             ReducedMatrixFunctional param,
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


}
