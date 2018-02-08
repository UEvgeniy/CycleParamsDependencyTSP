package model;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Parameters {

    private Parameters(){}


    public static Double uniqueRoutes(TSPReducedMatrix matrix){

        Set<Integer> unique = new HashSet<>();
        for (int x: matrix.getMinRoutes()){
            unique.add(x);
        }

        return (double)unique.size();
    }
}
