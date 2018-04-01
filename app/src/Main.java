import control.*;
import io.ObjReducedMatrixLoader;
import io.ObjReducedMatrixSaver;
import model.BindedData;
import io.TxtComplexityLoader;
import io.TxtMatrixLoader;
import model.*;
import util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Main {

    public static void main (String[] args) {
        try {
            Dataset<TSPReducedMatrix> dReduced =
                    UseCases.getTxtTSPAndConvert(args[0], true, ReducingOrder.RowsColumns);

            Dataset<Complexity> dComplexity = UseCases.loadComplexities(args[1]);

            UseCases.bindDatasets(dReduced, dComplexity);

            UseCases.experiment(dReduced, dComplexity, Parameters::maxCycleLength, new PearsonCorrelation());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
