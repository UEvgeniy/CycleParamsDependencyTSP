package control;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataBinderTest {

    @Test
    public void bind() {
        Assert.assertEquals("msg", 4, 2+2);
    }
}