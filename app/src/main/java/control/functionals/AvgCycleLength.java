package control.functionals;

import model.TSPReducedMatrix;

public class AvgCycleLength implements ReducedMatrixFunctional {
    @Override
    public Double apply(TSPReducedMatrix matrix) {
        return cycleLength(matrix)
                .stream()
                .mapToDouble(a -> a)
                .average().orElse(0.0);
    }

    @Override
    public String toString() {
        return "Average length of cycle";
    }
}
