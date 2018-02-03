package model;

import java.util.Map;
import java.util.HashMap;

public class Dataset<T> {


    private Map<Integer, T> dataMap;


    public Dataset(){
        dataMap = new HashMap<>();
    }

    public void add(int id, T matrix){

        if (dataMap.containsKey(id)){
            String EXC = "Id " + id + " is already exists in the dataset.";
            throw new IllegalArgumentException(EXC);
        }

        dataMap.put(id, matrix);
    }
}
