package model;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Dataset<T> implements Serializable {

    private static final long serialVersionUID = -868575552503575995L;
    private Map<Integer, T> dataMap;

    public Dataset(){
        dataMap = new HashMap<>();
    }

    public void add(int id, T object){
        if (dataMap.containsKey(id)){
            String EXC = "Id " + id + " is already exists in the dataset.";
            throw new IllegalArgumentException(EXC);
        }
        dataMap.put(id, object);
    }

    public T getById(int id){
        return dataMap.get(id);
    }

    public Set<Integer> getKeys(){
        return dataMap.keySet();
    }

    public int size() {
        return dataMap.size();
    }
}
