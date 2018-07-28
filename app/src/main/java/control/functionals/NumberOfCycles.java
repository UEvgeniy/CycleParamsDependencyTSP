package control.functionals;

import model.TSPReducedMatrix;

public class NumberOfCycles implements ReducedMatrixFunctional {
    @Override
    public Double apply(TSPReducedMatrix matrix) {
        return (double)matrix.getCycles().size();
    }

    @Override
    public String toString() {
        return "Number of cycles";
    }
}
