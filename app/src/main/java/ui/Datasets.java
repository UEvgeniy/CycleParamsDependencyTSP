package ui;

import model.Dataset;

class Datasets<T, U> {

    private final Dataset<T> first;
    private final Dataset<U> second;

    Dataset<T> getFirst() {
        return first;
    }

    Dataset<U> getSecond() {
        return second;
    }


    Datasets(Dataset<T> first, Dataset<U> second){
        this.first = first;
        this.second = second;
    }
}
