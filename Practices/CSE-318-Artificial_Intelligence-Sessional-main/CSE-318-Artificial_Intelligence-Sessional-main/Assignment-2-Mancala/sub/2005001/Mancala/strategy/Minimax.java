package strategy;

import mancala.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Minimax {
    public int max_depth;
    public int heuristic_id;
    public Minimax(int max_depth, int heuristic_id) {
        this.max_depth = max_depth;
        this.heuristic_id = heuristic_id;
    }
    public int[] run(State state, int alpha, int beta, int depth){
        if(state.isLeaf() || depth==max_depth){
            return new int[]{Heuristic.get_heuristic(state, heuristic_id), -1};
        }
        ArrayList<State> nextStates = state.getNextStates();
        Collections.shuffle(nextStates);
        int bestMoveIndex = -1;
        if(state.isMax()){
            for(State nextState: nextStates) {
                int[] arr = run(nextState, alpha, beta, depth + 1);
                int score = arr[0];
                if (score > alpha) {
                    alpha = score;
                    bestMoveIndex = nextState.lastMovedPitIndex;
                }
                if (alpha >= beta) {
                    break;
                }
            }
            return new int[]{alpha, bestMoveIndex};
        } else {
            for(State nextState: nextStates) {
                int[] arr = run(nextState, alpha, beta, depth + 1);
                int score = arr[0];
                if (score < beta) {
                    beta = score;
                    bestMoveIndex = nextState.lastMovedPitIndex;
                }
                if (alpha >= beta) {
                    break;
                }
            }
            return new int[]{beta, bestMoveIndex};
        }
    }
}
