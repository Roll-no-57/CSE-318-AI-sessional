
import java.util.*;

interface Heuristic {
    int calculate(Board board);
}

class Board {
    private int[][] tiles;
    private int size;
    private int blankRow, blankCol;

    public Board(int[][] tiles) {
        this.size = tiles.length;
        this.tiles = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.tiles[i][j] = tiles[i][j];
                if (tiles[i][j] == 0) {
                    blankRow = i;
                    blankCol = j;
                }
            }
        }
    }

    public int getSize() { return size; }
    public int[][] getTiles() { return tiles; }
    public int getBlankRow() { return blankRow; }
    public int getBlankCol() { return blankCol; }

    public boolean isGoal() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int expected = (i * size + j + 1) % (size * size);
                if (tiles[i][j] != expected) return false;
            }
        }
        return true;
    }

    public List<Board> getNeighbors() {
        List<Board> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, Down, Left, Right

        for (int[] dir : directions) {
            int newRow = blankRow + dir[0];
            int newCol = blankCol + dir[1];
            if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size) {
                int[][] newTiles = new int[size][size];
                for (int i = 0; i < size; i++)
                    newTiles[i] = tiles[i].clone();
                newTiles[blankRow][blankCol] = newTiles[newRow][newCol];
                newTiles[newRow][newCol] = 0;
                neighbors.add(new Board(newTiles));
            }
        }
        return neighbors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Arrays.deepEquals(tiles, board.tiles);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(tiles);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.append(tiles[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

class HammingDistance implements Heuristic {
    @Override
    public int calculate(Board board) {
        int distance = 0;
        int[][] tiles = board.getTiles();
        int size = board.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (tiles[i][j] != 0 && tiles[i][j] != (i * size + j + 1) % (size * size)) {
                    distance++;
                }
            }
        }
        return distance;
    }
}

class ManhattanDistance implements Heuristic {
    @Override
    public int calculate(Board board) {
        int distance = 0;
        int[][] tiles = board.getTiles();
        int size = board.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (tiles[i][j] != 0) {
                    int value = tiles[i][j];
                    int goalRow = (value - 1) / size;
                    int goalCol = (value - 1) % size;
                    distance += Math.abs(i - goalRow) + Math.abs(j - goalCol);
                }
            }
        }
        return distance;
    }
}

class EuclideanDistance implements Heuristic {
    @Override
    public int calculate(Board board) {
        double distance = 0;
        int[][] tiles = board.getTiles();
        int size = board.getSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (tiles[i][j] != 0) {
                    int value = tiles[i][j];
                    int goalRow = (value - 1) / size;
                    int goalCol = (value - 1) % size;
                    distance += Math.sqrt(Math.pow(i - goalRow, 2) + Math.pow(j - goalCol, 2));
                }
            }
        }
        return (int) Math.round(distance);
    }
}

class LinearConflict implements Heuristic {
    private ManhattanDistance manhattan = new ManhattanDistance();

    @Override
    public int calculate(Board board) {
        int manhattanDistance = manhattan.calculate(board);
        int conflicts = 0;
        int[][] tiles = board.getTiles();
        int size = board.getSize();

        // Check rows
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - 1; j++) {
                for (int k = j + 1; k < size; k++) {
                    if (tiles[i][j] != 0 && tiles[i][k] != 0) {
                        int val1 = tiles[i][j];
                        int val2 = tiles[i][k];
                        int goalRow1 = (val1 - 1) / size;
                        int goalRow2 = (val2 - 1) / size;
                        if (goalRow1 == i && goalRow2 == i && (val1 - 1) % size > (val2 - 1) % size) {
                            conflicts++;
                        }
                    }
                }
            }
        }

        // Check columns
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size - 1; i++) {
                for (int k = i + 1; k < size; k++) {
                    if (tiles[i][j] != 0 && tiles[k][j] != 0) {
                        int val1 = tiles[i][j];
                        int val2 = tiles[k][j];
                        int goalCol1 = (val1 - 1) % size;
                        int goalCol2 = (val2 - 1) % size;
                        if (goalCol1 == j && goalCol2 == j && (val1 - 1) / size > (val2 - 1) / size) {
                            conflicts++;
                        }
                    }
                }
            }
        }

        return manhattanDistance + 2 * conflicts;
    }
}

class Node implements Comparable<Node> {
    Board board;
    int g; // Cost from start
    int h; // Heuristic estimate
    Node parent;

    public Node(Board board, int g, int h, Node parent) {
        this.board = board;
        this.g = g;
        this.h = h;
        this.parent = parent;
    }

    public int getF() { return g + h; }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.getF(), other.getF());
    }
}

class AStarSolver {
    private Heuristic heuristic;
    private int nodesExplored;
    private int nodesExpanded;

    public AStarSolver(Heuristic heuristic) {
        this.heuristic = heuristic;
        this.nodesExplored = 0;
        this.nodesExpanded = 0;
    }

    public List<Board> solve(Board initial) {
        PriorityQueue<Node> openList = new PriorityQueue<>();
        Set<Board> closedList = new HashSet<>();
        Map<Board, Node> nodeMap = new HashMap<>();

        int h = heuristic.calculate(initial);
        Node startNode = new Node(initial, 0, h, null);
        openList.add(startNode);
        nodeMap.put(initial, startNode);
        nodesExplored++;

        while (!openList.isEmpty()) {
            Node current = openList.poll();
            nodesExpanded++;
            closedList.add(current.board);

            if (current.board.isGoal()) {
                List<Board> path = new ArrayList<>();
                Node node = current;
                while (node != null) {
                    path.add(node.board);
                    node = node.parent;
                }
                Collections.reverse(path);
                return path;
            }

            for (Board neighbor : current.board.getNeighbors()) {
                if (!closedList.contains(neighbor)) {
                    int newG = current.g + 1;
                    Node existing = nodeMap.get(neighbor);
                    if (existing == null || newG < existing.g) {
                        int newH = heuristic.calculate(neighbor);
                        Node newNode = new Node(neighbor, newG, newH, current);
                        openList.add(newNode);
                        nodeMap.put(neighbor, newNode);
                        nodesExplored++;
                    }
                }
            }
        }
        return null; // No solution
    }

    public int getNodesExplored() { return nodesExplored; }
    public int getNodesExpanded() { return nodesExpanded; }
}

class Solver {
    public static boolean isSolvable(Board board) {
        int size = board.getSize();
        int[] flat = new int[size * size];
        int[][] tiles = board.getTiles();
        int blankRowFromBottom = 0;

        // Flatten the board and find blank row
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                flat[i * size + j] = tiles[i][j];
                if (tiles[i][j] == 0) {
                    blankRowFromBottom = size - i;
                }
            }
        }

        // Count inversions
        int inversions = 0;
        for (int i = 0; i < flat.length - 1; i++) {
            for (int j = i + 1; j < flat.length; j++) {
                if (flat[i] != 0 && flat[j] != 0 && flat[i] > flat[j]) {
                    inversions++;
                }
            }
        }

        if (size % 2 == 1) { // Odd grid size
            return inversions % 2 == 0;
        } else { // Even grid size
            return (blankRowFromBottom % 2 == 0) == (inversions % 2 == 1);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int size = scanner.nextInt();
        int[][] tiles = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                tiles[i][j] = scanner.nextInt();
            }
        }

        Board initial = new Board(tiles);
        if (!isSolvable(initial)) {
            System.out.println("Unsolvable puzzle");
            return;
        }

        // Use Manhattan Distance as default heuristic
        AStarSolver solver = new AStarSolver(new LinearConflict());
        List<Board> solution = solver.solve(initial);

        if (solution != null) {
            System.out.println("Minimum number of moves = " + (solution.size() - 1));
            for (Board board : solution) {
                System.out.println(board);
            }
            System.out.println("Nodes explored: " + solver.getNodesExplored());
            System.out.println("Nodes expanded: " + solver.getNodesExpanded());
        } else {
            System.out.println("No solution found");
        }
    }
}

