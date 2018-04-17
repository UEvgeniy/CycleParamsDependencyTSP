package control;

import model.TSPReducedMatrix;

import java.util.*;
import java.util.stream.Collectors;

public class Parameters {

    private class Cycle{

        private Integer[] cycle;

        Cycle(Integer[] values){
            cycle = values;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cycle cycle1 = (Cycle) o;
            return cycle_equals(this.cycle, cycle1.cycle);
        }

        private boolean cycle_equals(Integer[] one, Integer[] two){
            if (one == null || two == null) {
                return false;
            }
            if (one.length != two.length){
                return false;
            }
            int start_index = 0;

            while (!one[0].equals(two[start_index])){
                start_index++;
                if (start_index == two.length){
                    return false;
                }
            };

            for (int i = 1; i < one.length; i++){
                if (!one[i].equals(two[++start_index % two.length])){
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = cycle.length;
            for (int i : cycle){
                hash *= i;
            }
            return hash * 157;
        }
    }

    private Parameters(){
    }

    public static List<Integer> getCycles(TSPReducedMatrix matrix){

        Set<Cycle> result = new HashSet<>();

        boolean[] visited = new boolean[matrix.getMinRoutes().length];

        for (int i = 0; i < visited.length; i++){
            if (visited[i]){
                continue;
            }
            makeStep(i, matrix.getMinRoutes(), new ArrayList<>(), result, visited);
        }

        return result.stream().map(a -> a.cycle.length).collect(Collectors.toList());
    }

    private static void makeStep(int node, final List<Integer>[] minRoutes, List<Integer> currentRoute,
                                Set<Cycle> result, boolean[] visited){

        if (currentRoute.contains(node)){


            Cycle cycle = new Parameters().new Cycle(
                    currentRoute.subList(
                            currentRoute.indexOf(node), currentRoute.size()
                    ).toArray(new Integer[0]));

            result.add(cycle);
            return;
        }

        visited[node] = true;
        currentRoute.add(node);
        for (Integer next_node: minRoutes[node]) {
            makeStep(next_node, minRoutes, new ArrayList<>(currentRoute), result, visited);
        }
    }

    //////////////// PARAMS OF REDUCED MATRIX //////////////

    public static Double uniqueCyclesNum(TSPReducedMatrix matrix){
        Set<Integer> unique = new HashSet<>(getCycles(matrix));
        return (double)unique.size();
    }

    public static Double cyclesNum(TSPReducedMatrix matrix){
        return (double)getCycles(matrix).size();
    }

    public static Double averageCycleLength(TSPReducedMatrix matrix){

        return getCycles(matrix)
                .stream()
                .mapToDouble(a -> a)
                .average().orElse(0.0);
    }

    public static Double maxCycleLength(TSPReducedMatrix matrix){

        return getCycles(matrix)
                .stream()
                .mapToDouble(a -> a)
                .max().orElse(0.0);
    }

    public static Double minCycleLength(TSPReducedMatrix matrix){

        return getCycles(matrix)
                .stream()
                .mapToDouble(a -> a)
                .min().orElse(0.0);
    }

    public static Double sumCycleLength(TSPReducedMatrix matrix){

        return getCycles(matrix)
                .stream()
                .mapToDouble(a -> a)
                .sum();
    }

    public static Double avgMultipleMaxLength(TSPReducedMatrix matrix){
        return maxCycleLength(matrix) * averageCycleLength(matrix);
    }

    public static Double dispersion(TSPReducedMatrix matrix){
        double avg = averageCycleLength(matrix);

        return Math.sqrt(getCycles(matrix)
                .stream()
                .map((c) -> ((c - avg) * (c - avg)))
                .reduce(0.0, (x,y) -> x + y) / (getCycles(matrix).size() - 1));

    }

}
