package model;

import java.util.List;
import java.util.Objects;

public class TSPReducedMatrix {

    private List<Integer>[] minRoutes;

    public TSPReducedMatrix(List<Integer>[] minRoutes){
        Objects.requireNonNull(minRoutes);
        this.minRoutes = minRoutes;
    }


    public List<Integer>[] getMinRoutes() {
        return minRoutes;
    }
}
