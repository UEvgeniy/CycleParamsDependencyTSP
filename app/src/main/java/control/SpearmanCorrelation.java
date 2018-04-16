package control;

import java.util.List;

public class SpearmanCorrelation implements Correlation {

    @Override
    public double count(List<Double> one, List<Double> another){
        // todo count
        return 0.0;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
