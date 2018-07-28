package control.functionals;

import model.TSPReducedMatrix;

public class TotalCycleLength implements ReducedMatrixFunctional {
    @Override
    public Double apply(TSPReducedMatrix matrix) {
        return cycleLength(matrix)
                .stream()
                .mapToDouble(a -> a)
                .sum();
    }

    @Override
    public String toString() {
        return "Total length of all cycles";
    }
}
