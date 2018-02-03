import control.MatrixConverter;
import io.DatasetLoader;
import io.TxtComplexityLoader;
import io.TxtMatrixLoader;
import model.Complexity;
import model.Dataset;
import model.TSPMatrix;
import model.TSPReducedMatrix;

public class Main {

    public static void main (String[] args) {

        // todo initiate directory path
        TxtMatrixLoader mLoader = new TxtMatrixLoader();
        TxtComplexityLoader cLoader = new TxtComplexityLoader();

        Dataset<TSPMatrix> datasetMatr = mLoader.load();
        Dataset<Complexity> datasetCompl = cLoader.load();

        // Dataset<TSPReducedMatrix> reduced = MatrixConverter::convertToReducedDataset(datasetMatr);


        System.out.println("TSP will be implemented soon");
    }

}
