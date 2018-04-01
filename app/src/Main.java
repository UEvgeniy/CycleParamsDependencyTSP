import control.Parameters;
import control.PearsonCorrelation;
import model.Complexity;
import model.Dataset;
import model.TSPReducedMatrix;

import java.io.IOException;

public class Main {

    public static void main (String[] args) {
        long start = System.currentTimeMillis();

        experiment(args);

        System.out.println((System.currentTimeMillis() - start) / 1000 + " sec");
    }

    public static void experiment(String[] args){
        try {

            Dataset<TSPReducedMatrix> dReduced = UseCases.deserializeReducedTSP(args[0], true);
            Dataset<Complexity> dComplexity = UseCases.loadComplexities(args[1]);

            double res = UseCases.experiment(
                    dReduced,
                    dComplexity,
                    Parameters::numberOfCycles,
                    new PearsonCorrelation());
            
            System.out.println(res);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
