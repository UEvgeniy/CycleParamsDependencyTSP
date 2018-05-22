package control.functionals;

import model.TSPReducedMatrix;

public class CycleLenDeviation implements ReducedMatrixFunctional {
    @Override
    public Double apply(TSPReducedMatrix matrix) {
        double avg = new AvgCycleLength().apply(matrix);

        return Math.sqrt(cycleLength(matrix)
                .stream()
                .map((c) -> ((c - avg) * (c - avg)))
                .reduce(0.0, (x,y) -> x + y) / (cycleLength(matrix).size() - 1));
    }

    @Override
    public String toString() {
        return "Deviation of cycle Lengths";
    }
}
