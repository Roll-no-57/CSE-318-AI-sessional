package mancala;

import player.Player;
import strategy.Minimax;

import java.util.Scanner;

import static util.Config.*;

public class Game {
    public Player[] players = new Player[2];
    public State currentState;

    public Game(Player p0, Player p1) {
        // id 0 means, in the console UI, that player will move from the down side
        // and id 1 means, will move from upper side
        if(!(p0.id==0 && p1.id==1)){
            System.out.println("Error! Players expected to have ids 0 and 1 respectively");
        }
        players[0] = p0;
        players[1] = p1;

        // player with id 0 always moves first
        int firstPlayerId = 0;

        currentState = new State(firstPlayerId);
    }

    public int getSideIndex(int totalIndex, int currentPlayerId){
        if(currentPlayerId == 0){
            return totalIndex + 1;
        } else {
            return totalIndex - 6;
        }
    }
    public int getTotalIndex(int sideIndex, int currentPlayerId){
        if(currentPlayerId == 0){
            return sideIndex - 1;
        } else {
            return sideIndex + 6;
        }
    }

    public void printCurrentState(){
        String spaces = "                    ";
        System.out.println(spaces + players[1].name + "' side");
        System.out.print(currentState);
        System.out.println(spaces + players[0].name + "' side\n");
    }

    // one single step
    public void step(Boolean verbose){
        Player thisStepPlayer = players[currentState.currentPlayerId];

        int initialAdditionalMoves = currentState.additionalMoves[thisStepPlayer.id];

        if(verbose) {
            System.out.println("It is " + thisStepPlayer.name + "' turn.");
        }

        // initialized to default
        int givenMoveIndex = -1;

        if(thisStepPlayer.isHuman){
            System.out.println("Please input a number between 1 and 6 to move from your side: ");

            Scanner scanner = new Scanner(System.in);
            int input = 0;

            while (true) {
                if (scanner.hasNextInt()) {
                    input = scanner.nextInt();

                    if (input < 1 || input > 6) {
                        System.out.println("Invalid input! Please enter a number between 1 and 6.");
                    } else {
                        int totalIndex = getTotalIndex(input, currentState.currentPlayerId);
                        if(currentState.canMoveFrom(totalIndex)){
                            givenMoveIndex = totalIndex;
                            break;
                        } else {
                            System.out.println("Sorry! You cannot move from pit " + input + " of your side.");
                        }
                    }
                } else {
                    System.out.println("Invalid input! Please enter a valid integer.");
                    scanner.next();
                }
            }
        } else {
            if(players[1- thisStepPlayer.id].isHuman){
                if(verbose){
                    System.out.println("\nSearching for Optimal Move...\n");
                }
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            Minimax minimax = new Minimax(thisStepPlayer.depth, thisStepPlayer.heuristic_id);

            // initial situation
            int[]arr = minimax.run(currentState, -INFINITY, INFINITY, 0);
            givenMoveIndex = arr[1];

        }

        int givenMoveSideIndex = getSideIndex(givenMoveIndex, currentState.currentPlayerId);

        currentState = currentState.getNextState(givenMoveIndex);

        if(verbose){
            System.out.println(thisStepPlayer.name + " moved from pit " + givenMoveSideIndex);
            System.out.println("\nCurrent Board:\n");
            printCurrentState();
            if(currentState.additionalMoves[thisStepPlayer.id] > initialAdditionalMoves){
                System.out.println(thisStepPlayer.name + " gained an additional move!\n");
            }
        }
    }

    public int run(Boolean verbose) {
        if(verbose){
            System.out.println("\nInitial Board:\n");
            printCurrentState();
        }

        while (!currentState.isLeaf()){
            step(verbose);
        }
        int[] finalTotalStones = currentState.getFinalTotalStones();

        if(verbose){
            System.out.println("\n");
            System.out.println(players[0].name + " -> " + finalTotalStones[0] + " stones, " + players[1].name + " -> " + finalTotalStones[1] + " stones");
        }

        int winningPlayerId;
        if(finalTotalStones[0] == finalTotalStones[1]){
            winningPlayerId = -1;
        } else{
            if(finalTotalStones[0] > finalTotalStones[1]){
                winningPlayerId = 0;
            } else {
                winningPlayerId = 1;
            }
        }

        if(verbose){
            if(winningPlayerId == -1){
                System.out.println("Draw !!!");
            } else {
                System.out.println(players[winningPlayerId].name + " won the game!");
            }
            System.out.println("\n");
        }
        return winningPlayerId;
    }
}
