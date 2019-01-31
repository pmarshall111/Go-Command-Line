package game;


import java.util.*;

public class Go {
    static final String xAxisLabels = "ABCDEFGHJKLMNOPQRSTUVWXYZ";

    private Board board;
    private BoardCoordinates lastMove = null;

    public Go(int squareSize) {
        this(squareSize, squareSize);
    }
    public Go(int height, int width) {
        if (height <= 0 || width <= 0) {
            throw new IllegalArgumentException("Board needs to have positive dimensions");
        } else if (width > xAxisLabels.length()) {
            throw new IllegalArgumentException("Board is too wide");
        }

        board = new Board(height, width);
    }

    public static void main(String[] args) {
        Go g = new Go(9);
        g.printBoardHistory();
    }

    public char[][] getBoard() {
        return board.getCurrentBoard();
    }

    public void move(String position) {
        BoardCoordinates movePosition = new BoardCoordinates(position, board.getHeight());
        try {
            board.addMove(movePosition);
            lastMove = movePosition;
        } catch (IllegalArgumentException e) {
            //we catch higher up. Only included so we can set the last move if no exception found.
            throw e;
        }
    }
    public void move(String... args) {
        for (String s: args) move(s);
    }


    public char getPosition(String position) {
        BoardCoordinates boardCoordinates = new BoardCoordinates(position, board.getHeight());
        return board.getPiece(boardCoordinates);
    }

    public void handicapStones(int numbStones) {
        board.placeHandicapStones(numbStones);
    }


    public void rollBack(int turns) {
        if (turns > board.getNumbMoves()) {
            throw new IllegalArgumentException("Cannot go back more moves than we've made");
        }

        board.goBackTurns(turns);
    }

    public void reset() {
        int currHeight = board.getHeight();
        int currWidth = board.getWidth();

        board = new Board(currHeight, currWidth);
    }

    public HashMap<String, Integer> getSize() {
        return board.getSize();
    }

    public String getTurn() {
        Pieces currPlayer = board.getCurrentPlayer();
        return currPlayer.name();
    }

    public Pieces getCurrPlayer() {
        return board.getCurrentPlayer();
    }

    public void passTurn() {
        board.addBlankBoard();
    }


    public String printBoard() {
        return print(getBoard());
    }

    private String print(char[][] board) {
        int sizeOfEachChar = 3;

        StringBuilder builder = new StringBuilder();
        addLetterKeys(builder, board[0], sizeOfEachChar);

        for (int i = 0; i<board.length; i++) {
            builder.append(String.format("%" + sizeOfEachChar + "d", this.board.getHeight()-i));
            for (int j = 0; j<board[i].length; j++) {
                char c = board[i][j];

                if (lastMove != null && lastMove.y == i && lastMove.x == j) {
                    c = Character.toUpperCase(c); //TODO: needs testing
                }
                builder.append(String.format("%" + sizeOfEachChar + "c", c));
            }
            builder.append("\n");
        }

        System.out.println(builder.toString());
        return builder.toString();
    }

    private void addLetterKeys(StringBuilder builder, char[] firstRow, int sizeOfEachChar) {
        builder.append(String.format("%" + sizeOfEachChar + "c", ' '));
        for (int i = 0; i<firstRow.length; i++) {
            builder.append(String.format("%" + sizeOfEachChar + "c", xAxisLabels.charAt(i)));
        }
        builder.append("\n");
    }

    public void printBoardHistory() {
        ArrayList<char[][]> hist = board.getBoardHistory();
        hist.forEach(this::print);
    }

    public boolean gameHasEnded() {
        return board.bothPlayersPassed() || board.hasNoEmptySpaces();
    }

    public int[] getScores() {
        HashMap<Pieces, Integer> scores = board.getCurrentScore();

        return new int[]{scores.get(Pieces.black), scores.get(Pieces.white)};
    }

}


