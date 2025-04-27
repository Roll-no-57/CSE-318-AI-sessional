import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Board{
    private int dimension;
    private int[][] tiles;
    private int blankRow,blankCol;


    // Constructor
    public Board(int[][] tiles) {
        this.dimension = tiles.length;
        this.tiles = new int[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            this.tiles[i] = tiles[i].clone();
        }
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (tiles[i][j] == 0) {
                    this.blankCol = j;
                    this.blankRow = i;
                    break;
                }
            }
        }
    }

    // checks if the puzzle is solvable or not ;
    public boolean isSolvable() {
        int size = getDimension();
        int[][] tiles = getTiles();
        int[] flat = new int[size * size];
        int blankRowFromBottom = 0;

        // flattening the board to count inversion by row major
        int index = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                flat[index] = tiles[i][j];
                if (tiles[i][j] == 0) {
                    blankRowFromBottom = size - i;
                }
                index++;
            }
        }

        // Count inversions
        int inversions = 0;
        for (int i = 0; i < flat.length; i++) {
            for (int j = i + 1; j < flat.length; j++) {
                if (flat[i] != 0 && flat[j] != 0 && flat[i] > flat[j]) {
                    inversions++;
                }
            }
        }

        // Now we have to decide if solvable
        if (size % 2 == 1) {
            // As board size is odd, puzzle is solvable if inversions are even
            if (inversions % 2 == 0) {
                return true;
            } else {
                return false;
            }
        } else {
            // If board size is even
            if ((blankRowFromBottom % 2 == 0 && inversions % 2 == 1) ||
                    (blankRowFromBottom % 2 == 1 && inversions % 2 == 0)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public List<Board> getNeighbor() {
        int[][] Directions = {{0,-1},{0,1},{1,0},{-1,0}};
        List<Board> neighbors = new ArrayList<>();

        for(int[] dir : Directions) {
            int newRow = blankRow+dir[0];
            int newCol = blankCol+dir[1];

            if((newRow >= 0 && newRow < dimension) && (newCol >= 0 && newCol < dimension)) {

                int[][] newTiles = new int[dimension][dimension];
                for(int i = 0; i < dimension; i++) {
                    for(int j = 0; j < dimension; j++) {
                        newTiles[i][j] = tiles[i][j];
                    }
                }

                // Swap the tiles
                newTiles[blankRow][blankCol] = newTiles[newRow][newCol];
                newTiles[newRow][newCol] = 0;


                Board newBoard = new Board(newTiles);
                neighbors.add(newBoard);
            }
        }
        return neighbors;
    }

    // To check if the board configuration reached in goal configuration .
    public boolean isGoal(){

        for(int i =0; i<dimension;i++){
            for(int j=0;j<dimension;j++){
                int expectedValue = (i*dimension + j + 1) % (dimension * dimension);
                if(tiles[i][j] != (expectedValue)){
                    return false;
                }
            }
        }
        return true;
    }


    // Check equal if two board configuration are same
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Board board)) return false;
        return blankRow == board.blankRow && blankCol == board.blankCol && Objects.deepEquals(tiles, board.tiles);
    }

    // Generate hashCode for full board configuration using deepHash for 2d arrays
    @Override
    public int hashCode() {
        return Objects.hash(Arrays.deepHashCode(tiles), blankRow, blankCol);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                sb.append(tiles[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // getter and setters
    public int[][] getTiles() {
        return tiles;
    }

    public void setTiles(int[][] tiles) {
        this.tiles = tiles;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public int getBlankRow() {
        return blankRow;
    }

    public void setBlankRow(int blankRow) {
        this.blankRow = blankRow;
    }

    public int getBlankCol() {
        return blankCol;
    }

    public void setBlankCol(int blankCol) {
        this.blankCol = blankCol;
    }


    public void printBoard()
    {
        for(int i =0;i<dimension;i++){
            for(int j=0;j<dimension;j++){
                System.out.println(tiles[i][j]+" ");
            }
            System.out.println("\n");
        }
    }
}