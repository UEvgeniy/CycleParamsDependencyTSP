package util;

import java.util.*;

public class Utils {

    private Utils(){

    }

    public static <K extends Comparable<? super K>, V extends Comparable<? super V>> Map<K, V>
    sort(Map<K, V> map, Comparator<? super Map.Entry<K, V>> comparator) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        list.sort(comparator);

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }


    public static class Compare{
        public static int byKeys(Map.Entry<Integer, Integer> a, Map.Entry<Integer, Integer> b){
            return b.getKey().compareTo(a.getKey());
        }

        public static int byValues(Map.Entry<Integer, Integer> a, Map.Entry<Integer, Integer> b){
            return b.getValue().compareTo(a.getValue());
        }
    }

}
