package control.functionals;

import model.TSPReducedMatrix;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ReducedMatrixFunctionalTest {

    // Tested method:
    // private static List<Integer> cycleLength(model.TSPReducedMatrix matrix)

    @Test
    public void testCycleLen() {

        /// Test 1. Several cycles ///
        List<Integer>[] minRoutes = formMinRoute(
                list(1, 3),
                list(2),
                list(3),
                list(4, 0),
                list(0)
        );
        testGettingCycle(minRoutes, 2, 3, 4, 5);

        /// Test 2. One cycle ///
        minRoutes = formMinRoute(
                list(2),
                list(3),
                list(1),
                list(0)
        );
        testGettingCycle(minRoutes, 4);

        /// Test 3. Chaos ///
        minRoutes = formMinRoute(
                list(4, 7),
                list(0),
                list(6),
                list(0, 2),
                list(3),
                list(1),
                list(2, 5),
                list(4)
        );
        testGettingCycle(minRoutes, 2, 3, 4, 7, 8);

        /// Test 4. Strong connection ///
        minRoutes = formMinRoute(
                list(1, 2),
                list(2, 0),
                list(0, 1)

        );
        testGettingCycle(minRoutes, 2, 2, 2, 3, 3);

        /// Test 5. Without max possible cycle ///
        minRoutes = formMinRoute(
                list(1, 3),
                list(2),
                list(1),
                list(2),
                list(3, 5),
                list(6),
                list(0, 4)

        );
        testGettingCycle(minRoutes, 2, 3);

    }

    /// Private methods
    private void testGettingCycle(List<Integer>[] minRoutes, Integer... expected) {

        // Object for testing default method in interface
        ReducedMatrixFunctional mock = matrix -> 0.;

        TSPReducedMatrix reducedMatrix = new TSPReducedMatrix(minRoutes);
        List<Integer> actual = mock.cycleLength(reducedMatrix);

        Assert.assertEquals("Number of cycles is not equal", expected.length, actual.size());


        for (int exp : expected){
            Assert.assertTrue("Expected cycle ".concat(Integer.toString(exp)).concat(", but not found"),
                    actual.contains(exp));
        }

        actual.removeAll(Arrays.asList(expected));
        Assert.assertTrue("Extra cycles found", actual.isEmpty());
    }

    @SafeVarargs
    private final List<Integer>[] formMinRoute(List<Integer>... values){
        return values;
    }

    private List<Integer> list(Integer... values){
        return Arrays.asList(values);
    }
}