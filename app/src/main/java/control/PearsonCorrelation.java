package control;

import java.util.List;
import java.util.stream.Collectors;

public class PearsonCorrelation implements Correlation {

    /*
        rxy = sum((dx)(dy)) / sqrt(sum(dx2)) * sqrt(sum(dy2))
    */
    @Override
    public double count(List<Double> one, List<Double> another){
        List<Double> dOne = deltas(one);
        List<Double> dAnother = deltas(another);

        double numerator = 0.0;

        for (int i = 0; i < dOne.size(); i++){
            numerator += dOne.get(i) * dAnother.get(i);
        }

        double denumerator = Math.sqrt(dOne.stream().map((c) -> (c * c)).reduce(0.0, (x,y) -> x + y)) *
                Math.sqrt(dAnother.stream().map((c) -> (c * c)).reduce(0.0, (x,y) -> x + y));

        return numerator / denumerator;
    }

    private List<Double> deltas(List<Double> list){

        double average = list.stream().reduce(0.0, (x,y) -> x+y) / list.size();
        return list.stream().map((c) -> (c - average)).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}