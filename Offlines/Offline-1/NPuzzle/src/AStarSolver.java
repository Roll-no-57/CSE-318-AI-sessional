import java.util.*;

public class AStarSolver {

    Heuristic heuristic;
    int nodeExplored;
    int nodeExpanded;

    public AStarSolver(Heuristic heuristic ){
        this.heuristic = heuristic;
        this.nodeExpanded = 0;
        this.nodeExplored = 0;

    }
    public List<Board> solve(Board initialBoard){

        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<Board> closedList = new HashSet<>();
        Map<Board,Node> nodeMap = new HashMap<>();

        int gCost = 0;
        int hCost = this.heuristic.calculate(initialBoard);
        Node startNode = new Node(initialBoard,gCost,hCost,null);

        openList.add(startNode);
        nodeMap.put(initialBoard,startNode);
        nodeExplored++;

        while(!openList.isEmpty()){

            Node promisingNode = openList.poll();
            closedList.add(promisingNode.getBoard());
            nodeExpanded++;

            if(promisingNode.getBoard().isGoal()){
                List<Board> path = new ArrayList<>();
                path.add(promisingNode.getBoard());
                Node currentNode = promisingNode ;

                while(currentNode.getParent() != null ){
                    currentNode = currentNode.getParent();
                    path.add(currentNode.getBoard());
                }
//                path.add(currentNode.getBoard());
                Collections.reverse(path);
                return path;
            }

            // Now expand all its neighbors but how to get neighbors?
            for(Board neighborBoard : promisingNode.getBoard().getNeighbor()){

                if(!closedList.contains(neighborBoard)){
                    Node neighborNode = nodeMap.get(neighborBoard);
                    int newGcost = promisingNode.getgCost() + 1;

                    if(neighborNode ==null || newGcost < neighborNode.getgCost()) {
                        int newHcost = heuristic.calculate(neighborBoard);
                        // Now we will create new Node even if it already exists.
                        // Reason is if we just update the gCost(as hCost is same as previous) it will not update the priority queue .
                        // priority queue only re-organize only if we insert something in it.
                        Node newNode = new Node(neighborBoard,newGcost,newHcost,promisingNode);

                        // insert it in priority queue and map
                        openList.add(newNode);
                        nodeMap.put(neighborBoard,newNode);
                        nodeExplored++;

                    }
                }
            }
        }
        return null;
    }
    public int getNodeExplored(){
        return nodeExplored;
    }
    public int getNodeExpanded(){
        return nodeExpanded;
    }
}
