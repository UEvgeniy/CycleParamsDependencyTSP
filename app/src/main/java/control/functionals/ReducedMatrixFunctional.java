package control.functionals;

import model.Cycle;
import model.TSPReducedMatrix;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@FunctionalInterface
public interface ReducedMatrixFunctional extends Function<TSPReducedMatrix, Double> {

    default List<Integer> cycleLength(TSPReducedMatrix matrix){
        return matrix.getCycles().stream().map(Cycle::length).collect(Collectors.toList());
    }
}
