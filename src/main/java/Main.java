import game.Go;
import game.Pieces;

import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    private static Scanner scanner;
    private static Go go;
    private static boolean resigned = false;

    private static HashSet<Integer> validHandicapSizes;
    static{
        validHandicapSizes = new HashSet<Integer>(){{add(9); add(13); add(19);}};
    }

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        init();
    }

    private static void init() {
        System.out.println("Hello!\n");
        System.out.println("What size game would you like to play?");
        int size = createGame();
        scanner.nextLine();

        if (validHandicapSizes.contains(size)) {
            System.out.println("Would Player 1 ('x') like any handicap stones?");
            String s = scanner.nextLine();

            if (s.toUpperCase().contains("YES") || s.toUpperCase().equals("Y")) {
                placeHandicapStones(size);
                scanner.nextLine();
            }
        }

        printHelp();
        startGame();
    }

    private static int createGame() {
        try {
            int size = scanner.nextInt();
            go = new Go(size);
            return size;
        } catch (IllegalArgumentException | InputMismatchException e) {
            System.out.println("Sorry the Go Board must be a size between 1 and 25.\nPlease enter a new size:");
            scanner.nextLine();
            return createGame();
        }
    }

    private static void placeHandicapStones(int gameSize) {
        askHandicapsQuestion(gameSize);
        try {
            int numbHandicaps = scanner.nextInt();
            go.handicapStones(numbHandicaps);
        } catch (IllegalArgumentException e) {
            System.out.println("Cannot place that number of stones");
            throw e;
        } catch (InputMismatchException e) {
            System.out.println("Please enter a number.");
            throw e;
        } catch(Exception e) {
            scanner.nextLine();
            placeHandicapStones(gameSize);
        }

    }

    private static void askHandicapsQuestion(int gameSize) {
        StringBuilder builder = new StringBuilder();
        builder.append("You may place up to ");
        builder.append(gameSize == 9 ? "5" : "9");
        builder.append(" handicap stones. How many would you like?");
        System.out.println(builder.toString());
    }

    private static void startGame() {
        System.out.println("Game starting!\n");
        while (!go.gameHasEnded() && !resigned) {
            try {
                go.printBoard();
                tryMove();
            } catch (IllegalArgumentException e) {
                System.out.println("\nCould not make that move. The problem was: " + e.getMessage());
                System.out.println("Try again:");
            }
        }

        if (!resigned) {
            printScores(true);
            askToPlayAgain();
        }
    }

    private static void printScores(boolean gameEnded) {
        int[] finalGameScores = go.getScores();
        StringBuilder builder = new StringBuilder();

        if (gameEnded) builder.append("The final scores were...\n");
        else builder.append("Current scores are...\n");

        builder.append("Player ONE: ");
        builder.append(finalGameScores[0]);
        builder.append(", Player TWO: ");
        builder.append(finalGameScores[1]);
        builder.append("\n");

        if (gameEnded) {
            if (finalGameScores[0] > finalGameScores[1]) {
                builder.append("PLAYER ONE WINS!");
            } else if (finalGameScores[0] == finalGameScores[1]) {
                builder.append("DRAW!");
            } else {
                builder.append("PLAYER TWO WINS!");
            }
        }

        System.out.println(builder.toString());
    }

    private static void tryMove() throws IllegalArgumentException {
        String move = askForMove().toUpperCase();

        if (move.contains("PASS") || move.equals("P")) {
            go.passTurn();
            System.out.println("You have passed your turn. If the next player passes the game will end.");
        } else if (move.contains("HELP") || move.equals("H")) {
            printHelp();
        } else if (move.contains("RESIGN") || move.equals("R")) {
            endGame();
        } else if (move.contains("SCORE") || move.equals("S")) {
            printScores(false);
        } else if (move.contains("UNDO") || move.equals("U")) {
            go.rollBack(1);
            System.out.println("Undoing the last move.");
            System.out.println(getPlayerMove());
        } else {
            go.move(move);
        }
    }

    private static void endGame() {
        printScores(false);
        System.out.println("Player " + go.getCurrPlayer().playerNumb + " resigned!");
        System.out.println("Player " + (go.getCurrPlayer().playerNumb == 1 ? 2 : 1) + " WINS!");
        askToPlayAgain();
    }

    private static void askToPlayAgain() {
        System.out.println("Would you like to start a new game?");

        while(true) {
            String decision = scanner.nextLine().toUpperCase();

            if (decision.contains("YES") || decision.equals("Y")) {
                init();
                return;
            } else if (decision.contains("NO") || decision.equals("N")) {
                resigned = true;
                System.out.println("Okay, see you soon.");
                return;
            } else {
                System.out.println("I'm sorry Dave, I'm afraid I can't do that");
            }
        }

    }

    private static String askForMove() {
        String player = getPlayerMove();
        System.out.println(player + " Type your move in the format 1A:");
        return scanner.nextLine();
    }

    private static String getPlayerMove() {
        Pieces p = go.getCurrPlayer();
        return "Player " + p.playerNumb + " ('" + p.stone + "') it is your turn!";
    }

    private static void printHelp() {
        StringBuilder builder = new StringBuilder();
        builder.append("HELP MENU:\n");
        builder.append("1. To make a move, type the row number and then the column letter e.g. 9B or 15Q\n");
        builder.append("2. To see the current score, type 'score' or press s\n");
        builder.append("3. To undo a move, type 'undo' or press u\n");
        builder.append("3. If you'd like to pass your turn, type 'pass' or press p\n");
        builder.append("4. A player can resign by typing 'resign' or press r\n");
        builder.append("5. To end the game naturally, both players need to pass one after the other.\n");
        builder.append("6. To see this menu again, type 'help'\n");

        System.out.println(builder.toString());
    }
}
