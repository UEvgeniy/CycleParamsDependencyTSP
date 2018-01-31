package model;

import model.TSPMatrix;

import java.util.Map;
import java.util.HashMap;

public class TSPMatrixDataset {


    private Map<Integer, TSPMatrix> matrixMap;


    public TSPMatrixDataset(){
        matrixMap = new HashMap<>();
    }

    public void add(int id, TSPMatrix matr){

        if (matrixMap.containsKey(id)){
            String EXC = "Id " + id + " is already exists in the dataset.";
            throw new IllegalArgumentException(EXC);
        }

        matrixMap.put(id, matr);
    }



    //public model.TSPMatrixDataset(Folder)

    // add map from ids to model.TSPMatrix
}
