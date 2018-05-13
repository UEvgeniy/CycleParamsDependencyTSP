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

    // Количество уникальных длин циклов
    public static Double uniqueCyclesNum(TSPReducedMatrix matrix){
        Set<Integer> unique = new HashSet<>(cycleLength(matrix));
        return (double)unique.size();
    }

    // Количество циклов
    public static Double cyclesNum(TSPReducedMatrix matrix){
        return (double)matrix.getCycles().size();
    }

    // Средняя длина цикла
    public static Double averageCycleLength(TSPReducedMatrix matrix){

        return cycleLength(matrix)
                .stream()
                .mapToDouble(a -> a)
                .average().orElse(0.0);
    }

    // Максимальная длина цикла
    public static Double maxCycleLength(TSPReducedMatrix matrix){

        return cycleLength(matrix)
                .stream()
                .mapToDouble(a -> a)
                .max().orElse(0.0);
    }

    // Минимальная длина цикла
    public static Double minCycleLength(TSPReducedMatrix matrix){

        return cycleLength(matrix)
                .stream()
                .mapToDouble(a -> a)
                .min().orElse(0.0);
    }

    // Сумма длин всех циклов
    public static Double sumCycleLength(TSPReducedMatrix matrix){

        return cycleLength(matrix)
                .stream()
                .mapToDouble(a -> a)
                .sum();
    }

    // Среднее на максималное
    public static Double avgMultipleMaxLength(TSPReducedMatrix matrix){
        return maxCycleLength(matrix) * averageCycleLength(matrix);
    }

    // Стандартное отклонение длин циклов
    public static Double deviation(TSPReducedMatrix matrix){
        double avg = averageCycleLength(matrix);

        return Math.sqrt(cycleLength(matrix)
                .stream()
                .map((c) -> ((c - avg) * (c - avg)))
                .reduce(0.0, (x,y) -> x + y) / (cycleLength(matrix).size() - 1));
    }

    // Среднее число циклов, проходящее через вершину (считаются только вершины циклов, иначе это sumCycleLength / N)
    public static Double avgConsistence(TSPReducedMatrix matrix){
        int[] vertices = new int[matrix.getMinRoutes().length];

        for (Cycle cycle : matrix.getCycles()){
            for (int vert : cycle.getCycle()){
                vertices[vert]++;
            }
        }

        int nonZero = 0;
        for (int i: vertices){
            if (i != 0){
                nonZero++;
            }
        }
        return sumCycleLength(matrix) / nonZero;
    }
}
