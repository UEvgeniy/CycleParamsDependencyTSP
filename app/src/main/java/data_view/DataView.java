package data_view;

import control.Parameters;
import model.BindedData;
import model.Complexity;
import model.TSPReducedMatrix;
import util.Utils;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataView {
    public static void list(BindedData<TSPReducedMatrix, Complexity> data, PrintStream ps) {

        List<TSPReducedMatrix> reduced = data.getFirst();
        List<Complexity> comp = data.getSecond();

        List<Binded> sorted = IntStream.range(0, reduced.size())
                .mapToObj(i -> new Binded(reduced.get(i), comp.get(i))) // Create the instance
                .sorted(Comparator.comparingInt(b -> b.complexity))              // Sort using a Comparator
                .collect(Collectors.toList());


        for (Binded b : sorted){
            int counter = 0;
            ps.print(++counter + ". ");
            ps.print("Compl: " + b.complexity);

            Map<Integer, Integer> map = countCycles(b.matr);
            ps.print(". Size: "+ map.values().stream().mapToInt(Integer::intValue).sum() + ": ");

            map = Utils.sort(map, Utils.Compare::byKeys);
            for (Integer key: map.keySet()){
                ps.print("\'" + key + "\'-" + map.get(key) + "шт; ");
            }
            ps.println();
        }

    }

    public static void table(BindedData<TSPReducedMatrix, Complexity> data, PrintStream ps, String sep){

        List<TSPReducedMatrix> reduced = data.getFirst();
        List<Complexity> comp = data.getSecond();

        // todo make sorting as method
        List<Binded> sorted = IntStream.range(0, reduced.size())
                .mapToObj(i -> new Binded(reduced.get(i), comp.get(i))) // Create the instance
                .sorted(Comparator.comparingInt(b -> b.complexity))              // Sort using a Comparator
                .collect(Collectors.toList());


        for (Binded b : sorted){

            ps.print(b.complexity);
            ps.print(sep);

            Map<Integer, Integer> map = countCycles(b.matr);
            map = Utils.sort(map, Utils.Compare::byKeys);


            for (int key = 1; key < b.matr.getMinRoutes().length; key++){ // todo change
                ps.print(map.get(key) == null ? 0 : map.get(key));
                ps.print(sep);
            }
            ps.println();
        }
    }

    public static void dot_diagram(BindedData<TSPReducedMatrix, Complexity> data, PrintStream ps){
        List<TSPReducedMatrix> reduced = data.getFirst();
        List<Complexity> comp = data.getSecond();

        List<Binded> sorted = IntStream.range(0, reduced.size())
                .mapToObj(i -> new Binded(reduced.get(i), comp.get(i))) // Create the instance
                .sorted(Comparator.comparingInt(b -> b.complexity))              // Sort using a Comparator
                .collect(Collectors.toList());

        ps.println("Complexity;Min;Max;Avg;Dev;");
        for (Binded b : sorted){
            ps.print(String.format("%d;", b.complexity));
            ps.print(String.format("%1$,.2f;", Parameters.minCycleLength(b.matr)));
            ps.print(String.format("%1$,.2f;", Parameters.maxCycleLength(b.matr)));
            ps.print(String.format("%1$,.2f;", Parameters.averageCycleLength(b.matr)));
            ps.println(String.format("%1$,.2f;", Parameters.dispersion(b.matr)));
        }

    }



    private static class Binded implements Comparable<Binded>{
        TSPReducedMatrix matr;
        Integer complexity;
        private Binded(TSPReducedMatrix m, Complexity c){
            this.matr = m;
            this.complexity = c.get();
        }

        @Override
        public int compareTo(Binded o) {
            return complexity - o.complexity;
        }
    }

    private static Map<Integer, Integer> countCycles(TSPReducedMatrix rm){
        Map<Integer, Integer> map = new HashMap<>();
        List<Integer> cycles = Parameters.getCycles(rm);

        for (Integer j : cycles){
            if (map.containsKey(j)){
                map.put(j, map.get(j) + 1);
            }
            else{
                map.put(j, 1);
            }
        }
        return map;
    }
}
