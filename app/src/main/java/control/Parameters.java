package control;

import model.Cycle;
import model.TSPReducedMatrix;


import java.util.*;
import java.util.stream.Collectors;

public class Parameters {

    private Parameters(){
    }


    public static List<Integer> cycleLength(TSPReducedMatrix matrix){
        return matrix.getCycles().stream().map(Cycle::length).collect(Collectors.toList());
    }

    //////////////// PARAMS OF REDUCED MATRIX //////////////

    public static Double uniqueCyclesNum(TSPReducedMatrix matrix){
        Set<Integer> unique = new HashSet<>(cycleLength(matrix));
        return (double)unique.size();
    }

    public static Double cyclesNum(TSPReducedMatrix matrix){
        return (double)cycleLength(matrix).size();
    }

    public static Double averageCycleLength(TSPReducedMatrix matrix){

        return cycleLength(matrix)
                .stream()
                .mapToDouble(a -> a)
                .average().orElse(0.0);
    }

    public static Double maxCycleLength(TSPReducedMatrix matrix){

        return cycleLength(matrix)
                .stream()
                .mapToDouble(a -> a)
                .max().orElse(0.0);
    }

    public static Double minCycleLength(TSPReducedMatrix matrix){

        return cycleLength(matrix)
                .stream()
                .mapToDouble(a -> a)
                .min().orElse(0.0);
    }

    public static Double sumCycleLength(TSPReducedMatrix matrix){

        return cycleLength(matrix)
                .stream()
                .mapToDouble(a -> a)
                .sum();
    }

    public static Double avgMultipleMaxLength(TSPReducedMatrix matrix){
        return maxCycleLength(matrix) * averageCycleLength(matrix);
    }

    public static Double dispersion(TSPReducedMatrix matrix){
        double avg = averageCycleLength(matrix);

        return Math.sqrt(cycleLength(matrix)
                .stream()
                .map((c) -> ((c - avg) * (c - avg)))
                .reduce(0.0, (x,y) -> x + y) / (cycleLength(matrix).size() - 1));
    }

}
