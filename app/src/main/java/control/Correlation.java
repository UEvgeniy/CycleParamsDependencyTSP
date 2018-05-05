package control;

import java.util.List;

public interface Correlation {

    double count(List<Double> one, List<Double> another);

    default void check(List<Double> one, List<Double> another){
        if (one == null || another == null){
            throw new IllegalArgumentException("Correlated data cannot be null");
        }
        if (one.size() < 3 || another.size() < 3){
            throw new IllegalArgumentException("Collections must have at least 3 elements");
        }
        if (one.size() != another.size()){
            throw new IllegalArgumentException("Correlated data must have equal size");
        }
    }
}
