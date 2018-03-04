package control;

import model.Dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DataBinder<T, P> {

    private Dataset<T> first;
    private Dataset<P> second;

    public DataBinder(Dataset<T> one, Dataset<P> another){
        this.first = Objects.requireNonNull(one);
        this.second = Objects.requireNonNull(another);
    }

    public BindedData<T, P> bind(){

        Set<Integer> fKeys = first.getKeys();
        Set<Integer> sKeys = second.getKeys();

        fKeys.retainAll(sKeys);

        List<T> f = new ArrayList<>();
        List<P> s = new ArrayList<>();

        for (int id: fKeys) {
            f.add(first.getById(id));
            s.add(second.getById(id));
        }

        return new BindedData<>(f, s);
    }
}
