import control.PearsonCorrelation;
import control.functionals.NumberOfCycles;
import io.ObjReducedDatasetLoader;
import model.Complexity;
import model.Dataset;
import model.TSPReducedMatrix;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main (String[] args) {
        long start = System.currentTimeMillis();

        //    tmp(args);

        System.out.println((System.currentTimeMillis() - start) / 1000 + " sec");
    }

    public static void tmp(String[] args) {
        try {

            ObjReducedDatasetLoader loader = new ObjReducedDatasetLoader(new File(args[2]));
            Dataset<TSPReducedMatrix> newD = loader.load();

            //UseCases.deserializeReducedTSP(args[0], false);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void experiment(String[] args){
        try {

            Dataset<TSPReducedMatrix> dReduced = UseCases.deserializeReducedTSP(args[0], true);
            Dataset<Complexity> dComplexity = UseCases.loadComplexities(args[1]);

            double res = UseCases.experiment(
                    dReduced,
                    dComplexity,
                    new NumberOfCycles(),
                    new PearsonCorrelation());

            System.out.println(res);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
