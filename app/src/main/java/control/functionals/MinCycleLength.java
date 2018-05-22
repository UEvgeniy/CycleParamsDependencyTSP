package control.functionals;

import model.TSPReducedMatrix;

public class MinCycleLength implements ReducedMatrixFunctional {
    @Override
    public Double apply(TSPReducedMatrix matrix) {
        return cycleLength(matrix)
                .stream()
                .mapToDouble(a -> a)
                .min().orElse(0.0);
    }

    @Override
    public String toString() {
        return "Minimum length of cycle";
    }
}
