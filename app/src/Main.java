import io.TSPDatasetLoader;
import io.TxtDatasetLoader;
import model.TSPMatrixDataset;

public class Main {

    public static void main (String[] args){

        // todo initiate directory path
        TSPDatasetLoader loader = new TxtDatasetLoader();

        TSPMatrixDataset dataset = loader.load();



        System.out.println("TSP will be implemented soon");
    }

}
