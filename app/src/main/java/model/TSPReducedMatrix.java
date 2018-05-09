package model;

import java.io.Serializable;
import java.util.*;

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

    /// Get cycles ///


    private Set<Cycle> cycles;

    public Set<Cycle> getCycles(){

        if (cycles != null){
            return cycles;
        }

        cycles = new HashSet<>();

        boolean[] visited = new boolean[minRoutes.length];

        for (int i = 0; i < visited.length; i++){
            if (visited[i]){
                continue;
            }
            makeStep(i, minRoutes, new ArrayList<>(), cycles, visited);
        }
        return cycles;
    }

    private void makeStep(int node, final List<Integer>[] minRoutes, List<Integer> currentRoute,
                                 Set<Cycle> result, boolean[] visited){

        if (currentRoute.contains(node)){


            Cycle cycle = new Cycle(
                    currentRoute.subList(
                            currentRoute.indexOf(node), currentRoute.size()
                    ).toArray(new Integer[0]));

            result.add(cycle);
            return;
        }

        visited[node] = true;
        currentRoute.add(node);
        for (Integer next_node: minRoutes[node]) {
            makeStep(next_node, minRoutes, new ArrayList<>(currentRoute), result, visited);
        }
    }
}
