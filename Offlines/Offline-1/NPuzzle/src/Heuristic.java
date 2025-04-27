interface Heuristic{
    int calculate(Board current);
}


//Counts the number of tiles that are not in their goal position.
//The blank tile is not included in this count.
class HammingDistance implements Heuristic{
    @Override
    public int calculate(Board initial){

        int notInPosition = 0 ;
        int[][] startTiles = initial.getTiles();
        int dimension = initial.getDimension();

        for(int i = 0 ; i<initial.getDimension(); i++){
            for(int j = 0;j<initial.getDimension();j++){

                int tile = startTiles[i][j];
                // If the tile is not blank
                if(tile != 0){
                    int correctRow = (tile - 1) / dimension;
                    int correctCol = (tile - 1)%dimension;

                    if(correctRow != i || correctCol != j){
                        notInPosition++;
                    }
                }
            }
        }

        return notInPosition;
    }
}

//Calculates the sum of the vertical and horizontal distances
//each tile must move to reach its goal position.
class ManhattanDistance implements Heuristic{
    @Override
    public int calculate(Board initial){


        int manDistance = 0 ;
        int[][] startTiles = initial.getTiles();
        int dimension = initial.getDimension();

        for(int i = 0 ; i<initial.getDimension(); i++){
            for(int j = 0;j<initial.getDimension();j++){

                int tile = startTiles[i][j];
                // If the tile is not blank
                if(tile != 0){
                    int correctRow =  (tile - 1) / dimension;
                    int correctCol = (tile - 1)%dimension;

                    if(correctRow != i || correctCol != j){
                        manDistance += Math.abs(correctRow - i) + Math.abs(correctCol - j);
                    }
                }
            }
        }

        return manDistance;
    }
}

//Computes the straight-line distance from each tile’s current
//position to its goal position
class EuclideanDistance implements Heuristic {
    @Override
    public int calculate(Board initial) {
        double euclideanDistance = 0;
        int[][] startTiles = initial.getTiles();
        int dimension = initial.getDimension();

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                int tile = startTiles[i][j];
                if (tile != 0) {
                    int correctRow = (tile - 1) / dimension;
                    int correctCol = (tile - 1) % dimension;
                    double dist = Math.sqrt(Math.pow(correctRow - i, 2) + Math.pow(correctCol - j, 2));
                    euclideanDistance += dist;
                }
            }
        }

        return (int) Math.round(euclideanDistance);
    }
}

class LinearConflict implements Heuristic {
    @Override
    public int calculate(Board initial) {
        int[][] startTiles = initial.getTiles();
        int dimension = initial.getDimension();

        ManhattanDistance manHatt = new ManhattanDistance();
        int manDistance = manHatt.calculate(initial);

        int linearConflict = 0;

        // Find the row conflicts
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                for (int k = j + 1; k < dimension; k++) {
                    int tileJ = startTiles[i][j];
                    int tileK = startTiles[i][k];

                    if (tileJ != 0 && tileK != 0) {
                        int expectedRowJ = (tileJ - 1) / dimension;
                        int expectedRowK = (tileK - 1) / dimension;

                        if ((expectedRowJ == i) && (expectedRowK == i) && (tileJ > tileK)) {
                            linearConflict++;
                        }
                    }
                }
            }
        }

        // Find the column conflicts
        for (int j = 0; j < dimension; j++) {
            for (int i = 0; i < dimension; i++) {
                for (int k = i + 1; k < dimension; k++) {
                    int tileI = startTiles[i][j];
                    int tileK = startTiles[k][j];

                    if (tileI != 0 && tileK != 0) {
                        int expectedColI = (tileI - 1) % dimension;
                        int expectedColK = (tileK - 1) % dimension;

                        if ((expectedColI == j) && (expectedColK == j) && (tileI > tileK)) {
                            linearConflict++;
                        }
                    }
                }
            }
        }

        return manDistance + 2 * linearConflict;
    }
}
