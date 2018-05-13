package control;

import model.BindedData;
import model.Dataset;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class DataBinderTest {

    @Test
    public void bind() {

        // Init datasets
        Dataset<String> strings = new Dataset<>();
        strings.add(42, "answer");
        strings.add(777, "azino");
        strings.add(13, "accident");
        strings.add(-1, "negative");

        Dataset<Integer> numbers = new Dataset<>();
        numbers.add(13, 66);
        numbers.add(777, 7);
        numbers.add(-100, -100);
        numbers.add(42, 420);

        // Init binded data
        DataBinder<String, Integer> db = new DataBinder<>(strings, numbers);
        BindedData<String, Integer> bindedData = db.bind();

        // Check correct size
        Set<Integer> intersection = new HashSet<>(strings.getKeys());
        intersection.retainAll(numbers.getKeys());
        Assert.assertEquals(
                "Binded data has incorrect size",
                bindedData.size(), intersection.size());

        // Check correct indexes
        for (int id : strings.getKeys()){
            if (numbers.getKeys().contains(id)){
                Assert.assertEquals("Data with same id not binded",
                        bindedData.getFirst().indexOf(strings.getById(id)),
                        bindedData.getSecond().indexOf(numbers.getById(id)));
            }
        }
    }
}