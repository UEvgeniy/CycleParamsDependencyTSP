package control;

import model.TSPReducedMatrix;

import java.util.function.Function;

@FunctionalInterface
public interface ReducedMatrixParameter extends Function<TSPReducedMatrix, Double> { }
