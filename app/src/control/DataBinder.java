package control;

import model.Dataset;

public class DataBinder<T, P> {



    public DataBinder(Dataset<T> one, Dataset<P> another){

    }

    public BindedData<T, P> bind(){
        BindedData<T, P> result = new BindedData<>();
        // todo bind
        return result;
    }
}
