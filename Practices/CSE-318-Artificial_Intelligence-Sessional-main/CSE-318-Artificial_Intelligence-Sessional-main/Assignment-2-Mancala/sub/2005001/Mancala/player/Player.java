package player;

import strategy.Heuristic;

public class Player {
    // player_id can be 0 or 1
    // 0 -> on side with 0...5 -> takes first move
    // 1 -> on side with 7...12 -> takes second move
    public int id;
    public String name;
    public int heuristic_id;
    public int depth;
    public Boolean isHuman;
    public Boolean takesFirstMove;

    public Player(int id, String name, Boolean isHuman) {
        this.id = id;
        this.name = name;
        this.isHuman = isHuman;
        // set defaults
        this.heuristic_id = 1;
        this.depth = 5;
    }

    public Player(int id, String name, int heuristic_id, int depth, Boolean isHuman) {
        this.id = id;
        this.name = name;
        this.heuristic_id = heuristic_id;
        this.depth = depth;
        this.isHuman = isHuman;
    }
}
