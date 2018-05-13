package control;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SpearmanCorrelation implements Correlation {

    @Override
    public double count(List<Double> one, List<Double> another){
        check(one, another);

        double[] first = rank(one);
        double[] second = rank(another);

        int size = first.length;

        // todo one line
        double summa = 0;
        for (int i = 0; i < size; i++){
            summa += (first[i] - second[i]) * (first[i] - second[i]);
        }

        return 1 - ((6 * summa) / (size * (size * size - 1)));
    }




    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    private double[] rank(List<Double> array){

        Pair[] pairs = new Pair[array.size()];

        for (int i = 0; i < pairs.length; i++){
            pairs[i] = new Pair(array.get(i), i);
        }

        Arrays.sort(pairs, Comparator.comparingDouble(a -> a.value));

        double[] res = new double[array.size()];

        int rank = 0;
        for (Pair p : pairs){
            res[p.index] = rank++;
        }

        for (int i = 1; i < pairs.length; i++){
            // If equal ranks founded
            if (pairs[i].value == pairs[i-1].value){
                int begin = i - 1;
                int end = i++;
                // Find end of repetitions
                while (i < pairs.length && pairs[i].value == pairs[begin].value){
                    end = i++;
                }
                // Update ranks
                for (int j = begin; j <= end; j++){
                    res[pairs[j].index] = (double)(begin + end) / 2;
                }
            }
        }

        return res;
    }

    // Class for ranking
    private class Pair{
        final double value;
        int index;

        Pair(double value, int index) {
            this.value = value;
            this.index = index;
        }
    }
}
