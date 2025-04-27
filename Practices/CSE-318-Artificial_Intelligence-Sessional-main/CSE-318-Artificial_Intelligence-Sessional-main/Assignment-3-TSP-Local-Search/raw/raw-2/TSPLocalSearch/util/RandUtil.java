package util;

import java.util.ArrayList;
import java.util.Random;

public class RandUtil {
    public static <T> T getRandomElement(ArrayList<T> lst){
        Random rand = new Random();
        int index = rand.nextInt(lst.size());
        return lst.get(index);
    }

    public static int getRandomOneIndexedNode(int n){
        Random rand = new Random();
        return 1 + rand.nextInt(n);
    }
}
