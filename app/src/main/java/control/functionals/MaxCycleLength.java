package control.functionals;

import model.TSPReducedMatrix;

public class MaxCycleLength implements ReducedMatrixFunctional {
    @Override
    public Double apply(TSPReducedMatrix matrix) {
        return cycleLength(matrix)
                .stream()
                .mapToDouble(a -> a)
                .max().orElse(0.0);
    }

    @Override
    public String toString() {
        return "Maximum length of cycle";
    }
}
