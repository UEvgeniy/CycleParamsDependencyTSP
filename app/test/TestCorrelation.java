import model.Correlation;
import model.PearsonCorrelation;
import org.junit.Assert;
import org.junit.Test;

import java.util.DuplicateFormatFlagsException;
import java.util.List;

public class TestCorrelation {

    @Test
    public void testPearson(){

        Correlation cor = new PearsonCorrelation();

        List<Double> one = List.of(1., 4., 8., 2., 9., 56., 23.);
        List<Double> two = List.of(-2., 8., 12., 6., 8., 122., 65.);

        Assert.assertEquals(0.985, cor.count(one, two), 0.01);
        Assert.assertEquals(0.985, cor.count(two, one), 0.01);
    }
}
