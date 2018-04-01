import control.Parameters;
import model.TSPReducedMatrix;

import org.junit.Assert;
import org.junit.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public class TestParameters {

    // Tested method:
    // private static List<Integer> getCycles(model.TSPReducedMatrix matrix)

    @Test
    public void testGetCycles() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

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

    // Tested method:
    // @override public boolean equals(Object o) (Parameters.Cycles)

    @Test
    public void testCyclesEquals() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        Constructor<Parameters> cParameters = Parameters.class.getDeclaredConstructor();
        cParameters.setAccessible(true);

        Parameters iParameters = cParameters.newInstance();

        Constructor cCycles = iParameters.getClass().getDeclaredClasses()[0].getDeclaredConstructors()[0];

        cCycles.setAccessible(true);

        Object obj = new Object();
        Object c327 = cCycles.newInstance(iParameters, new Integer[]{3, 2, 7});
        Object c3278 = cCycles.newInstance(iParameters, new Integer[]{3, 2, 7, 8});
        Object c372 = cCycles.newInstance(iParameters, new Integer[]{3, 7, 2});
        Object c732 = cCycles.newInstance(iParameters, new Integer[]{7, 3, 2});
        Object c273 = cCycles.newInstance(iParameters, new Integer[]{2, 7, 3});
        Object cNull = cCycles.newInstance(iParameters, null);

        Assert.assertEquals(c327, c273);
        Assert.assertEquals(c273, c732);

        Assert.assertNotEquals(c327, c3278);
        Assert.assertNotEquals(c327, c372);
        Assert.assertNotEquals(c273, cNull);
        Assert.assertNotEquals(c732, obj);
    }

    /// Private methods
    private void testGettingCycle(List<Integer>[] minRoutes, Integer... expected) {

        TSPReducedMatrix reducedMatrix = new TSPReducedMatrix(minRoutes);
        List<Integer> actual = Parameters.getCycles(reducedMatrix);

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