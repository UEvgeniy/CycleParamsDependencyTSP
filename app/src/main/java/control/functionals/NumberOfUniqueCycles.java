package control.functionals;

import model.TSPReducedMatrix;

import java.util.HashSet;
import java.util.Set;

public class NumberOfUniqueCycles implements ReducedMatrixFunctional {
    @Override
    public Double apply(TSPReducedMatrix matrix) {
        Set<Integer> unique = new HashSet<>(cycleLength(matrix));
        return (double)unique.size();
    }

    @Override
    public String toString() {
        return "Number of unique cycle lengths";
    }
}
