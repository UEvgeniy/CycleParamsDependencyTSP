package model;

import java.util.function.Function;

@FunctionalInterface
public interface ReducedMatrixParameter extends Function<TSPReducedMatrix, Double> { }
