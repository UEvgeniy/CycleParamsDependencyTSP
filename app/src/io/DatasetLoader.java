package io;
import model.Dataset;

public interface DatasetLoader<T> {

    Dataset<T> load();
}
