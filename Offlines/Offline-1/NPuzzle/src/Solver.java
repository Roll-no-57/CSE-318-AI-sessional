import java.util.List;
import java.util.Scanner;

public class Solver {

    public static Heuristic chooseHeuristic(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("No heuristic given. Using default: ManhattanDistance");
            return new ManhattanDistance();
        }

        name = name.trim().toLowerCase();

        switch (name) {
            case "hamming":
                return new HammingDistance();
            case "euclidean":
                return new EuclideanDistance();
            case "linearconflict":
                return new LinearConflict();
            case "manhattan":
                return new ManhattanDistance();
            default:
                System.out.println("Unknown heuristic. Using default: ManhattanDistance");
                return new ManhattanDistance();
        }
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Take heuristic input
        System.out.println("Enter heuristic (manhattan, hamming, euclidean, linearconflict) or press Enter for default:");
        String heuristicName = scanner.nextLine();

        // Choose heuristic
        Heuristic heuristic = chooseHeuristic(heuristicName);

        // Take board input
        int size = scanner.nextInt();
        int[][] tiles = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                tiles[i][j] = scanner.nextInt();
            }
        }

        Board initial = new Board(tiles);

        if (!initial.isSolvable()) {
            System.out.println("Unsolvable puzzle");
            return;
        }

        AStarSolver solver = new AStarSolver(heuristic);
        List<Board> solution = solver.solve(initial);

        if (solution != null) {
            System.out.println("Minimum number of moves = " + (solution.size() - 1));
            System.out.println();
            for (Board board : solution) {
                System.out.println(board);
            }
            System.out.println("Nodes explored: " + solver.getNodeExplored());
            System.out.println("Nodes expanded: " + solver.getNodeExpanded());
        } else {
            System.out.println("No solution found");
        }
    }
}


// Total 568 lines of code