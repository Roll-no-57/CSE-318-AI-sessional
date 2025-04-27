//////✅ Problem Statement:
//////You are given a grid (2D matrix) where:
//////
//////        0 represents a free cell.
//////
//////        1 represents a blocked cell (you can't go there).
//////
//////        You can move up, down, left, right.
//////
//////                                             You are given a start cell and a goal cell.
//////
//////        Use A* to find the shortest path from the start to the goal.
//////
//////✅ A* Heuristic:
//////We’ll use the Manhattan Distance as the heuristic function:
//////h(n) = |x1 - x2| + |y1 - y2|





import java.util.*;

class Cell implements Comparable<Cell> {
    int row, col;
    int gCost = Integer.MAX_VALUE, hCost = 0;
    Cell parent = null;

    Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    int fCost() {
        return gCost + hCost;
    }

    @Override
    public int compareTo(Cell other) {
        return Integer.compare(this.fCost(), other.fCost());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell c = (Cell) o;
        return row == c.row && col == c.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}

public class AStarSolver {
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public static List<Cell> findPath(int[][] grid, int[] start, int[] goal) {
        int rows = grid.length, cols = grid[0].length;

        // Create a reusable cell matrix
        Cell[][] cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Cell(r, c);
            }
        }

        Cell startCell = cells[start[0]][start[1]];
        Cell goalCell = cells[goal[0]][goal[1]];

        PriorityQueue<Cell> openSet = new PriorityQueue<>();
        boolean[][] closedSet = new boolean[rows][cols];

        startCell.gCost = 0;
        startCell.hCost = heuristic(startCell, goalCell);
        openSet.add(startCell);

        while (!openSet.isEmpty()) {
            Cell current = openSet.poll();

            if (current.equals(goalCell)) {
                return reconstructPath(current);
            }

            closedSet[current.row][current.col] = true;

            for (int[] dir : DIRECTIONS) {
                int newRow = current.row + dir[0];
                int newCol = current.col + dir[1];

                if (!isValid(grid, newRow, newCol)) continue;

                Cell neighbor = cells[newRow][newCol];
                if (closedSet[newRow][newCol]) continue;

                int tentativeG = current.gCost + 1;
                if (tentativeG < neighbor.gCost) {
                    neighbor.gCost = tentativeG;
                    neighbor.hCost = heuristic(neighbor, goalCell);
                    neighbor.parent = current;

                    if (openSet.contains(neighbor)) {
                        openSet.remove(neighbor); // remove the old version
                    }
                    openSet.add(neighbor); // re-insert with updated cost

                }
            }
        }

        return Collections.emptyList(); // No path found
    }

    private static int heuristic(Cell a, Cell b) {
        return Math.abs(a.row - b.row) + Math.abs(a.col - b.col);
    }

    private static boolean isValid(int[][] grid, int row, int col) {
        return row >= 0 && row < grid.length &&
                col >= 0 && col < grid[0].length &&
                grid[row][col] == 0;
    }

    private static List<Cell> reconstructPath(Cell end) {
        List<Cell> path = new ArrayList<>();
        for (Cell current = end; current != null; current = current.parent) {
            path.add(current);
        }
        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args) {
        int[][] grid = {
                {0, 0, 0, 0},
                {0, 1, 1, 0},
                {0, 0, 0, 0},
                {0, 1, 1, 0}
        };

        int[] start = {0, 0};
        int[] goal = {3, 3};

        List<Cell> path = findPath(grid, start, goal);

        if (path.isEmpty()) {
            System.out.println("No path found.");
        } else {
            System.out.println("Path:");
            for (Cell cell : path) {
                System.out.println("(" + cell.row + ", " + cell.col + ")");
            }
        }
    }
}









