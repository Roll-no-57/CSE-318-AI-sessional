import mancala.Game;
import player.Player;
import strategy.Heuristic;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        run_Human_vs_AI();
//        run_AI_vs_AI();
    }

    public static void run_Human_vs_AI() {
        Scanner scanner = new Scanner(System.in);

        int MIN_DEPTH = 1;
        int MAX_DEPTH = 10;

        System.out.print("Please enter your name: ");
        String name = scanner.nextLine();

        int depth = 0;
        while (true) {
            System.out.print("Enter opponent AI's depth (between " + MIN_DEPTH + " and " + MAX_DEPTH + "): ");
            if (scanner.hasNextInt()) {
                depth = scanner.nextInt();
                if (depth < MIN_DEPTH || depth > MAX_DEPTH) {
                    System.out.println("Invalid input!");
                } else {
                    break;
                }
            } else {
                System.out.println("Invalid input! Please enter a valid integer for depth.");
                scanner.next();
            }
        }

        int heuristic_id = 0;
        while (true) {
            System.out.print("Enter opponent AI's heuristic ID (between 1 and " + Heuristic.num_heuristics + "): ");
            if (scanner.hasNextInt()) {
                heuristic_id = scanner.nextInt();
                if (heuristic_id < 1 || heuristic_id > Heuristic.num_heuristics) {
                    System.out.println("Invalid input!");
                } else {
                    break;
                }
            } else {
                System.out.println("Invalid input! Please enter a valid integer for heuristic ID.");
                scanner.next();
            }
        }

        String humanPlaysFirst = "";
        while (true) {
            System.out.print("Do you want to play first? (Y/N): ");
            humanPlaysFirst = scanner.next().trim().toUpperCase();
            if (humanPlaysFirst.equals("Y") || humanPlaysFirst.equals("N")) {
                break;  // Valid input, exit the loop
            } else {
                System.out.println("Invalid input! Please enter 'Y' for Yes or 'N' for No.");
            }
        }

        Player p0 = null;
        Player p1 = null;

        if(humanPlaysFirst.equals("Y")){
            p0 = new Player(0, name, true);
            p1 = new Player(1, "AI", false);
        } else {
            p0 = new Player(0, "AI", false);
            p1 = new Player(1, name, true);
        }

        Game game = new Game(p0, p1);
        int winningPlayerId = game.run(true);

    }

    public static void run_AI_vs_AI() {
        System.out.println("Simulating games among AI heuristics...");

        Player p0 = new Player(0, "AI-1", false);
        Player p1 = new Player(1, "AI-2", false);

        int totalGames = 0;
        int draws = 0;
        int[] heuristic_wins = new int[Heuristic.num_heuristics + 1];
        int[] heuristic_losses = new int[Heuristic.num_heuristics + 1];

        int MIN_DEPTH = 4;
        int MAX_DEPTH = 8;

        int iterations = 1;

        String csvFile = "ai_vs_ai_simulation.csv";

        try (FileWriter fw = new FileWriter(csvFile);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println("FirstPlayerHeuristic,FirstPlayerDepth,SecondPlayerHeuristic,SecondPlayerDepth,WinningPlayer,WinningHeuristic,WinningDepth");


            for (int i = 0; i < iterations; i++) {
                for (int h0 = 1; h0 <= Heuristic.num_heuristics; h0++) {
                    for (int d0 = MIN_DEPTH; d0 <= MAX_DEPTH; d0++) {
                        for (int h1 = 1; h1 <= Heuristic.num_heuristics; h1++) {
                            for (int d1 = MIN_DEPTH; d1 <= MAX_DEPTH; d1++) {
                                p0.heuristic_id = h0;
                                p0.depth = d0;
                                p1.heuristic_id = h1;
                                p1.depth = d1;

                                Game game = new Game(p0, p1);
                                int winningPlayerId = game.run(false);
                                totalGames++;

                                int winningHeuristic = (winningPlayerId == -1) ? -1 : game.players[winningPlayerId].heuristic_id;
                                int winningDepth = (winningPlayerId == -1) ? -1 : game.players[winningPlayerId].depth;

                                if (winningPlayerId == -1) {
                                    draws++;
                                } else {
                                    heuristic_wins[game.players[winningPlayerId].heuristic_id]++;
                                    heuristic_losses[game.players[1-winningPlayerId].heuristic_id]++;
                                }

                                pw.println(h0 + "," + d0 + "," + h1 + "," + d1 + "," + (winningPlayerId+1) + "," + winningHeuristic + "," + winningDepth);

                            }
                        }
                    }
                }
            }

            System.out.println("\nTotal Games = " + totalGames + "\n");
            System.out.println(String.format("Games drawn = %d", draws));
            for (int j = 1; j <= Heuristic.num_heuristics; j++) {
//                double pct = (heuristic_wins[j] * 100.0 / totalGames);
//                System.out.println(String.format("Heuristic %d wins = %.2f%%", j, pct));
                int wins = heuristic_wins[j];
                int losses = heuristic_losses[j];
                double ratio = (wins*1.0)/losses;
                System.out.println(String.format("Heuristic %d -> wins = %d, losses = %d, ratio = %.2f:1", j, wins, losses, ratio));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
