package model;

public class Cycle{

    private final Integer[] cycle;

    public Integer[] getCycle(){
        return cycle;
    }

    Cycle(Integer[] values){
        cycle = values;
    }

    public int length(){
        return cycle.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cycle cycle1 = (Cycle) o;
        return cycle_equals(this.cycle, cycle1.cycle);
    }

    private boolean cycle_equals(Integer[] one, Integer[] two){
        if (one == null || two == null) {
            return false;
        }
        if (one.length != two.length){
            return false;
        }
        int start_index = 0;

        while (!one[0].equals(two[start_index])){
            start_index++;
            if (start_index == two.length){
                return false;
            }
        }

        for (int i = 1; i < one.length; i++){
            if (!one[i].equals(two[++start_index % two.length])){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = cycle.length;
        for (int i : cycle){
            hash *= i;
        }
        return hash * 157;
    }
}
