import control.BindedData;
import control.DataBinder;
import control.TSPConverter;
import io.TxtComplexityLoader;
import io.TxtMatrixLoader;
import model.*;


public class Main {

    public static void main (String[] args) {

        // todo initiate directory path
        TxtMatrixLoader mLoader = new TxtMatrixLoader();
        TxtComplexityLoader cLoader = new TxtComplexityLoader();

        Dataset<TSPMatrix> datasetMatr = mLoader.load();
        Dataset<Complexity> datasetCompl = cLoader.load();

        Dataset<TSPReducedMatrix> reduced = TSPConverter.toReducedDataset(datasetMatr);

        DataBinder<TSPReducedMatrix, Complexity> binder = new DataBinder<>(reduced, datasetCompl);
        BindedData<TSPReducedMatrix, Complexity> lists = binder.bind();

        
        ReducedMatrixParameter param = Parameters::count;
        Correlation correlation = new PearsonCorrelation(); // or new SpearmanCorrelation();


        double result = correlation.count(
                TSPConverter.toParamsDataset(lists.getFirst(), param),
                TSPConverter.toDouble(lists.getSecond())
        );


        System.out.println("TSP will be implemented soon " + result);
    }

}
