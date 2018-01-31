package model;

public class TSPMatrix {

    /*
    Fields
    */

    private int[][] matrix;
    private int dimension;


    /*
    Constructor
    */
    public TSPMatrix(int dimension) {

        if (dimension < 1) {
            String EXC_DIM_MES = "Dimension of Matrix must be positive.";
            throw new IllegalArgumentException(EXC_DIM_MES);
        }

        this.dimension = dimension;
        this.matrix = new int[dimension][dimension];
    }


    /*
    Public methods
    */
    public int get(int i, int j) {

        checkIndexes(i, j);
        return matrix[i][j];

    }

    void set(int i, int j, int value) {

        checkIndexes(i, j);
        matrix[i][j] = value;
    }


    /*
    Private methods
    */
    private void checkIndexes(int a, int b) {

        if (a >= dimension || b > dimension) {
            String EXC_IND_MES = "Index must be less than dimension.";
            throw new IllegalArgumentException(EXC_IND_MES);
        }

        if (a < 0 || b < 0) {
            String EXC_IND_MES = "Index must be positive.";
            throw new IllegalArgumentException(EXC_IND_MES);
        }
    }
}
