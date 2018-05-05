package control;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PearsonCorrelationTest {

    private PearsonCorrelation correlation = new PearsonCorrelation();

    private List<Double> nullList = null;
    private List<Double> emptyList = new ArrayList<>();
    private List<Double> good3 = Arrays.asList(0., 42., 777.);
    private List<Double> good5 = Arrays.asList(1., 2., 3., 4., 5.);

    @Test (expected = IllegalArgumentException.class)
    public void nullList() {
        correlation.count(nullList, good3);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyList() {
        correlation.count(emptyList, good5);
    }

    @Test (expected = IllegalArgumentException.class)
    public void difSize() {
        correlation.count(good5, good3);
    }

    @Test
    public void count() {
        List<Double> one = Arrays.asList(1., 4., 6., 8., 2.);
        List<Double> two = Arrays.asList(4., 6., 8., 2., 32.);
        Assert.assertEquals(-0.44, correlation.count(one, two), 0.01);

        one =  Arrays.asList(15.6, 77.2, 79.4, 82.3, 20.6, 85.2, 73.3, 80.7, 6.4, 75.4, 37.);
        two = Arrays.asList(4.4, 33.1, 94.7, 68.5, 46.5, 70.3, 32.9, 48.1, 38.8, 42.1, 70.3);

        Assert.assertEquals(0.45, correlation.count(one, two), 0.01);
    }
}