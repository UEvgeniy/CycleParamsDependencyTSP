package model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class TSPReducedMatrix implements Serializable{

    private static final long serialVersionUID = 4240191732724871683L;
    private List<Integer>[] minRoutes;

    public TSPReducedMatrix(List<Integer>[] minRoutes){
        Objects.requireNonNull(minRoutes);
        this.minRoutes = minRoutes;
    }

    public List<Integer>[] getMinRoutes() {
        return minRoutes;
    }
}
