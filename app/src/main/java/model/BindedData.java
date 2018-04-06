package model;

import java.util.List;
import java.util.Objects;

public class BindedData<T, P> {

    private List<T> first;
    private List<P> second;

    public BindedData(List<T> first, List<P> second){

        if (Objects.requireNonNull(first).size() != Objects.requireNonNull(second).size()){
            String EXC = "Two not null arrays must have equal length";
            throw new IllegalArgumentException(EXC);
        }

        this.first = first;
        this.second = second;
    }


    public List<T> getFirst(){
        return first;
    }

    public List<P> getSecond(){
        return second;
    }
}
