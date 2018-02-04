import control.BindedData;
import control.DataBinder;
import control.TSPConverter;
import io.TxtComplexityLoader;
import io.TxtMatrixLoader;
import model.*;
import java.io.File;

public class Main {

    public static void main (String[] args) {

        // Init location of data files
        File matrFile = new File(args[0]);
        File complFile = new File(args[1]);

        // Init data loaders
        TxtMatrixLoader mLoader = new TxtMatrixLoader(matrFile, true);
        TxtComplexityLoader cLoader = new TxtComplexityLoader(complFile);

        // Load data to datasets
        Dataset<TSPMatrix> datasetMatr = mLoader.load();
        Dataset<Complexity> datasetCompl = cLoader.load();

        // Convert TSP Matrixes to reduced ones
        Dataset<TSPReducedMatrix> reduced = TSPConverter.toReducedDataset(datasetMatr);

        // Bind reduced matrixes and its complexity by ids
        DataBinder<TSPReducedMatrix, Complexity> binder = new DataBinder<>(reduced, datasetCompl);
        BindedData<TSPReducedMatrix, Complexity> lists = binder.bind();

        // Select reduced matrix parameter
        ReducedMatrixParameter param = Parameters::count;

        // Select correlation coefficient
        Correlation correlation = new PearsonCorrelation(); // or new SpearmanCorrelation();

        // Count correlation between Reducrd Matrix Param and Complexity
        double result = correlation.count(
                TSPConverter.toParamsDataset(lists.getFirst(), param),
                TSPConverter.toDouble(lists.getSecond())
        );

        System.out.println("The result correlation is " + result);
    }
}
