package control;

import model.BindedData;
import model.Dataset;
import org.junit.Assert;
import org.junit.Test;

public class DataBinderTest {

    @Test
    public void bind() {

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


        DataBinder<String, Integer> db = new DataBinder<>(strings, numbers);
        BindedData<String, Integer> bindedData = db.bind();


        String msg = "Binded data cannot has size bigger than one of datasets";
        Assert.assertTrue(msg, bindedData.size() <= strings.size());
        Assert.assertTrue(msg, bindedData.size() <= numbers.size());

        for (int id : strings.getKeys()){
            if (numbers.getKeys().contains(id)){
                Assert.assertEquals("Data with same id not binded",
                        bindedData.getFirst().indexOf(strings.getById(id)),
                        bindedData.getSecond().indexOf(numbers.getById(id)));
            }
        }
    }
}