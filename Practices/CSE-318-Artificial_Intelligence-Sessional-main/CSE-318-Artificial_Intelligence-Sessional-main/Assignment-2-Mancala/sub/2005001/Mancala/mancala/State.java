package mancala;

import java.util.ArrayList;
import java.util.Arrays;

import static util.Config.*;

public class State {
    public int[] board;

    // here, assuming that the player at the side with pits 0..5 is Maximizer
    //  and, the player at the side with pits 7..12 is Minimizer
    public int currentPlayerId;   // 0 or 1

    // for two players
    public int[] additionalMoves;
    public int[] capturedStones;

    public int lastMovedPitIndex;

    // initial state
    public State(int currentPlayerId) {
        this.board = new int[total];
        for(int i=0; i<total; i++){
            if(i==mancalaIndices[0] || i==mancalaIndices[1]){
                board[i] = 0;
            } else {
                board[i] = INITIAL_STONES_ON_EACH_PIT;
            }
        }
        this.currentPlayerId = currentPlayerId;
        this.additionalMoves = new int[]{0,0};
        this.capturedStones = new int[]{0,0};
        this.lastMovedPitIndex = -1;
    }

    // creating deepcopy
    public State(State other) {
        this.board = Arrays.copyOf(other.board, other.board.length);
        this.currentPlayerId = other.currentPlayerId;
        this.additionalMoves = Arrays.copyOf(other.additionalMoves, other.additionalMoves.length);
        this.capturedStones = Arrays.copyOf(other.capturedStones, other.capturedStones.length);
        this.lastMovedPitIndex = -1;
    }

    public int[] getRangeForPlayer() {
        int firstPitIndex = (currentPlayerId == 0) ? 0 : (PITS_ON_EACH_SIDE + 1);
        int lastPitIndex = firstPitIndex + PITS_ON_EACH_SIDE - 1;

        return new int[] {firstPitIndex, lastPitIndex};
    }

    public Boolean isOwnSidePit(int pitIndex){
        int firstPitIndex, lastPitIndex;
        firstPitIndex = (currentPlayerId ==0) ? 0 : (PITS_ON_EACH_SIDE+1);
        lastPitIndex = firstPitIndex + PITS_ON_EACH_SIDE - 1;
        if(!(pitIndex >= firstPitIndex && pitIndex <= lastPitIndex)){
            return false;
        }
        return true;
    }

    public Boolean canMoveFrom(int pitIndex){
        if(!isOwnSidePit(pitIndex)){
            return false;
        }
        if(board[pitIndex]<=0){
            return false;
        }
        return true;
    }

    public State getNextState(int movedPitIndex){
        if(!canMoveFrom(movedPitIndex)){
            return null;    // move not possible
        }
        State nextState = new State(this);
        nextState.lastMovedPitIndex = movedPitIndex;
        nextState.board[movedPitIndex] = 0;
        int remainingStones = board[movedPitIndex];
        int startFrom = getNextPitIndex(movedPitIndex);
        int i;
        for(i=startFrom; remainingStones>0; i=getNextPitIndex(i)){
            if(i==mancalaIndices[1 - currentPlayerId]){
                continue;   // skip opponent's mancala
            }
            nextState.board[i]++;
            remainingStones--;
        }
        int lastDroppedPit = getPrevPitIndex(i);
        if(lastDroppedPit == mancalaIndices[currentPlayerId]){
            nextState.additionalMoves[currentPlayerId]++;
            nextState.currentPlayerId = currentPlayerId;
        } else {
            int oppositePit = getOppositePitIndex(lastDroppedPit);
            if(isOwnSidePit(lastDroppedPit) && (nextState.board[lastDroppedPit] == 1) && (nextState.board[oppositePit] > 0)){
                // capture
                int totalCaptured = nextState.board[lastDroppedPit] + nextState.board[oppositePit];
                nextState.board[mancalaIndices[currentPlayerId]] += totalCaptured;
                nextState.board[lastDroppedPit] = 0;
                nextState.board[oppositePit] = 0;
                nextState.capturedStones[currentPlayerId] += totalCaptured;
            }
            // no additional moves, so no player switching
            nextState.currentPlayerId = 1- currentPlayerId;
        }
        return nextState;
    }

    public ArrayList<State> getNextStates(){
        ArrayList<State>nextStates = new ArrayList<>();
        for(int i=0; i<total; i++){
            if(canMoveFrom(i)){
                nextStates.add(getNextState(i));
            }
        }
        return nextStates;
    }

    // in counterClockWise order
    private int getNextPitIndex(int pitIndex){
        return (pitIndex+1)%total;
    }

    // in counterClockWise order
    private int getPrevPitIndex(int pitIndex){
        return (pitIndex-1+total)%total;
    }

    int getOppositePitIndex(int pitIndex){
        return (2*PITS_ON_EACH_SIDE) - pitIndex;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
//        sb.append("\nCurrent Board:\n");
        String spaces = "      ";
        String divider = spaces + "-------------------------------------\n";
        sb.append(divider);
        sb.append(String.format("  %d  ", board[mancalaIndices[1]]));
        if(board[mancalaIndices[1]] < 10){
            sb.append(" ");
        }
        for(int i=total-2; i>=PITS_ON_EACH_SIDE+1; i--){
            sb.append(String.format("|  %d  ", board[i]));
        }
        sb.append("|\n");
        sb.append(divider);
        sb.append(spaces);
        for(int i=0; i<=PITS_ON_EACH_SIDE-1; i++){
            sb.append(String.format("|  %d  ", board[i]));
        }
        sb.append("|");
        sb.append(String.format("  %d  ", board[mancalaIndices[0]]));
        sb.append("\n");
        sb.append(divider);
//        sb.append(String.format("Now it is player %d's turn.\n", currentPlayerId));
//        sb.append("Last moved pit index = " + lastMovedPitIndex);
        return sb.toString();
    }

    // testing
    public static void main(String[] args) {
        State state = new State(0);
        System.out.println(state);
        state = state.getNextState(2);
        System.out.println(state);
        state = state.getNextState(3);
        System.out.println(state);
    }
    public int getStorageDifference(){
        return board[mancalaIndices[0]] - board[mancalaIndices[1]];
    }
    public int getSideStonesDifference(){
        int diff = 0;
        for(int i=0; i<=PITS_ON_EACH_SIDE-1; i++){
            diff += board[i];
        }
        for(int i=PITS_ON_EACH_SIDE+1; i<=total-2; i++){
            diff -= board[i];
        }
        return diff;
    }

    public Boolean isMax(){
        // assume player 0 to be maximizing
        // as we have defined the heuristics as such
        return (currentPlayerId == 0);
    }

    public int[] getSideStones(){
        int[] sideStones = new int[]{0,0};
        for(int i=0; i<=PITS_ON_EACH_SIDE-1; i++){
            sideStones[0] += board[i];
        }
        for(int i=PITS_ON_EACH_SIDE+1; i<=total-2; i++){
            sideStones[1] += board[i];
        }
        return sideStones;
    }

    public Boolean isLeaf(){
        int[] sideStones = getSideStones();
        return ((sideStones[0]==0) || (sideStones[1]==0));
    }

    public int[] getFinalTotalStones(){
        int[] sideStones = getSideStones();
        int[] finalTotalStones = new int[2];
        for(int i=0; i<2; i++){
            finalTotalStones[i] = board[mancalaIndices[i]] + sideStones[i];
        }
        return finalTotalStones;
    }
}
