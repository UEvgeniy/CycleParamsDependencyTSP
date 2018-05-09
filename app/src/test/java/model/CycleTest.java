package model;

import control.Parameters;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.junit.Assert.*;

public class CycleTest {

    @Test
    public void equals() {

        Object obj = new Object();
        Object c327 = new Cycle(new Integer[]{3, 2, 7});
        Object c3278 = new Cycle(new Integer[]{3, 2, 7, 8});
        Object c372 = new Cycle(new Integer[]{3, 7, 2});
        Object c732 = new Cycle(new Integer[]{7, 3, 2});
        Object c273 = new Cycle(new Integer[]{2, 7, 3});
        Object cNull = new Cycle(null);

        Assert.assertEquals(c327, c273);
        Assert.assertEquals(c273, c732);

        Assert.assertNotEquals(c327, c3278);
        Assert.assertNotEquals(c327, c372);
        Assert.assertNotEquals(c273, cNull);
        Assert.assertNotEquals(c732, obj);
    }
}