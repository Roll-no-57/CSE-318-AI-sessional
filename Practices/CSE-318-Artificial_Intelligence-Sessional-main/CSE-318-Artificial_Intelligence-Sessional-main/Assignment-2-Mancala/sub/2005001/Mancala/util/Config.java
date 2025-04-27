package util;

public class Config {
    public final static int PITS_ON_EACH_SIDE = 6;
    public final static int INITIAL_STONES_ON_EACH_PIT = 4;
    public static int total = 2 + 2*PITS_ON_EACH_SIDE;
    public static int[] mancalaIndices = new int[] {PITS_ON_EACH_SIDE, total - 1};

    public static int INFINITY = Integer.MAX_VALUE;
}
