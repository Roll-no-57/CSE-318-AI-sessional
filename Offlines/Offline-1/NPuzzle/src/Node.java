//think of each board configuration/state after moving 0 tile as a node

public class Node implements Comparable<Node> {
    //Here the priority value will be actual cost to reach this node and the heuristic value to this to final node
    private Board board;
    private int gCost;
    private int hCost;
    private Node parent;

    // Constructor
    public Node(Board board,int gCost,int hCost,Node parent){
        this.board = board;
        this.gCost = gCost;
        this.hCost = hCost;
        this.parent = parent;
    }

    public int getfCost(){
        return gCost + hCost;
    }

    @Override
    public int compareTo(Node other){
        return Integer.compare(this.getfCost(),other.getfCost());
    }


    // This equals checks if the two board configuration are same then they refer to the same Node
    @Override
    public boolean equals(Object obj){

        if(this == obj)return true;
        if(!(obj instanceof Node)){
            return false;
        }
        Node other = (Node)obj;
        return this.board.equals(other.board);
    }

    // This method is mainly used by the Hash-based collections like hasMap,HashSet
    @Override
    public int hashCode(){
        return board.hashCode();
    }

    //getters and setters
    public Board getBoard(){
        return this.board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public int getgCost() {
        return gCost;
    }

    public void setgCost(int gCost) {
        this.gCost = gCost;
    }

    public int gethCost() {
        return hCost;
    }

    public void sethCost(int hCost) {
        this.hCost = hCost;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
}
