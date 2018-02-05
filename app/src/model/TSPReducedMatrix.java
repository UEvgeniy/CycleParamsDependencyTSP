package model;

import java.util.Objects;

public class TSPReducedMatrix {

    private int[] minRoutes;

    public TSPReducedMatrix(int[] minRoutes){
        Objects.requireNonNull(minRoutes);
        this.minRoutes = minRoutes;
    }


    public int[] getMinRoutes() {
        return minRoutes;
    }
}
