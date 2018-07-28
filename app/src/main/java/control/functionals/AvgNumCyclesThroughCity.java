package control.functionals;

import model.Cycle;
import model.TSPReducedMatrix;

public class AvgNumCyclesThroughCity implements ReducedMatrixFunctional {
    @Override
    public Double apply(TSPReducedMatrix matrix) {
        int[] vertices = new int[matrix.getMinRoutes().length];

        for (Cycle cycle : matrix.getCycles()){
            for (int vert : cycle.getCycle()){
                vertices[vert]++;
            }
        }

        int nonZero = 0;
        for (int i: vertices){
            if (i != 0){
                nonZero++;
            }
        }
        return new TotalCycleLength().apply(matrix) / nonZero;
    }


    @Override
    public String toString() {
        return "Average number of cycle passing through the city";
    }

}
