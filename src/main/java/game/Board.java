package game;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.IntStream;

class Board {
    private ArrayList<char[][]> boardHistory;
    private HashMap<String, Integer> boardSize;
    private ActiveChains activeChains;
    private PositionOfHandicapStones handicapsForThisBoard;
    private boolean handicapsPlaced;

    private final String HEIGHT_KEY = "height";
    private final String WIDTH_KEY = "width";


    public Board(int height, int width) {
        createStartingBoard(height, width);

        this.boardSize = new HashMap<String, Integer>() {
            { put(HEIGHT_KEY, height); put(WIDTH_KEY, width); }
        };
        this.activeChains = new ActiveChains(this);
        this.handicapsForThisBoard = PositionOfHandicapStones.getHandicapsData(height, width);
        this.handicapsPlaced = false;
    }

    private void createStartingBoard(int height, int width) {
        char[][] startingBoard = IntStream.range(0, height)
                .mapToObj(x -> {
                    char[] inner = new char[width];
                    Arrays.fill(inner, '.');
                    return inner;}
                ).toArray(char[][]::new);


        this.boardHistory = new ArrayList<>();
        this.boardHistory.add(startingBoard);
    }

    public HashMap<String, Integer> getSize() {
        return boardSize;
    }

    ArrayList<char[][]> getBoardHistory() {
        return new ArrayList<>(this.boardHistory);
    }

    void addMove(BoardCoordinates movePosition) {
        if (moveIsOutOfBounds(movePosition)) {
            throw new IllegalArgumentException("Move is out of bounds");
        }
        else if (spaceIsAlreadyTaken(movePosition)) {
            throw new IllegalArgumentException("Space is already taken");
        }

        makeTheMove(movePosition);

        if (violatesKoRule()) {
            goBackTurns(1);
            throw new IllegalArgumentException("Violates KO rule.");
        } else if (activeChains.hasChainWithNoLiberties()) {
            goBackTurns(1);
            throw new IllegalArgumentException("Cannot self capture");
        }
    }

    private void makeTheMove(BoardCoordinates movePosition) {
        activeChains.addMoveToChains(movePosition, getCurrentPlayer());
        HashSet<BoardCoordinates> capturedPieces = activeChains.getCapturedStones();

        char[][] newBoard = removePiecesFromBoard(capturedPieces);
        newBoard[movePosition.y][movePosition.x] = getCurrentPlayer().stone;

        this.boardHistory.add(newBoard);
    }

    private boolean moveIsOutOfBounds(BoardCoordinates boardCoordinates) {
        return getPiece(boardCoordinates) == null;
    }

    private boolean spaceIsAlreadyTaken(BoardCoordinates boardCoordinates) {
        Character currChar = getPiece(boardCoordinates);
        return !currChar.equals('.');
    }

    private boolean violatesKoRule() {
        if (boardHistory.size() >= 3) {
            char[][] newBoard = copyBoard();
            int lastRecordIndex = boardHistory.size()-1;
            char[][] board2TurnsAgo = boardHistory.get(lastRecordIndex - 2);

            return Arrays.deepEquals(newBoard, board2TurnsAgo);
        }

        return false;
    }

    private char[][] removePiecesFromBoard(HashSet<BoardCoordinates> capturedPieces) {
        char[][] board = copyBoard();
        capturedPieces.forEach((BoardCoordinates coords) -> board[coords.y][coords.x] = Pieces.neutral.stone);

        return board;
    }

    char[][] getCurrentBoard() {
        return this.boardHistory.get(this.boardHistory.size()-1);
    }

    private char[][] copyBoard() {
        char[][] currBoard = getCurrentBoard();
        return Arrays.stream(currBoard)
                .map((char[] row) -> Arrays.copyOf(row, row.length))
                .toArray(char[][]::new);
    }

    public Character getPiece(BoardCoordinates boardCoordinates) {
        char[][] board =  getCurrentBoard();

        try {
            return board[boardCoordinates.y][boardCoordinates.x];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    Pieces getCurrentPlayer() {
        return Pieces.getCurrentPlayer(this.boardHistory.size()%2);
    }

    int getHeight() {
        return boardSize.get(HEIGHT_KEY);
    }

    int getWidth() {
        return boardSize.get(WIDTH_KEY);
    }

    //used when a player passes their turn
    void addBlankBoard() {
        char[][] newBoard = copyBoard();
        this.boardHistory.add(newBoard);
    }

    void placeHandicapStones(int stones) {
        checkForHandicapErrors(stones);
        this.handicapsPlaced = true;

        char[][] board = copyBoard();
        Pieces currPlayer = getCurrentPlayer();
        ArrayList<BoardCoordinates> stonePositions = handicapsForThisBoard.getStonesToPlace(stones);
        stonePositions.forEach(pos -> {
            board[pos.y][pos.x] = currPlayer.stone;
            activeChains.addNewChainAndUpdateCurrChains(pos, currPlayer);
        });

        resetBoardHistory(board);
    }

    private void checkForHandicapErrors(int numbStones) {
        if (cantTakeHandicaps()) {
            throw new IllegalArgumentException("PieceCharacter boardSize does not allow for handicap stones.");
        }
        else if (getNumbMoves() > 0) {
            throw new IllegalArgumentException("Cannot place handicap stones after first move has been made");
        }
        else if (hasPlacedHandicapStones()) {
            throw new IllegalArgumentException("Cannot place handicap stones again as they have already been placed");
        }
        else if (maxNumbHandicaps() < numbStones) {
            throw new IllegalArgumentException("Too many stones for this type of board");
        } else if (numbStones < 0) {
            throw new IllegalArgumentException("Cannot place a negative number of stones");
        }
    }

    private void resetBoardHistory(char[][] startingBoard) {
        this.boardHistory = new ArrayList<>();
        this.boardHistory.add(startingBoard);
    }


    private boolean cantTakeHandicaps() {
        return isNotSquare() || handicapsForThisBoard == null;
    }

    private boolean isNotSquare() {
        return getHeight() != getWidth();
    }

    int getNumbMoves() {
        return this.boardHistory.size() - 1;
    }

    private boolean hasPlacedHandicapStones() {
        return this.handicapsPlaced;
    }

    private int maxNumbHandicaps() {
        if (handicapsForThisBoard == null) return 0;
        else return handicapsForThisBoard.getMaxNumbHandicapStones();
    }

    void goBackTurns(int turns) {
        int placeToSlice = this.boardHistory.size();
        while (turns > 0) {
            placeToSlice--;
            turns--;
        }
        this.boardHistory = new ArrayList<>(this.boardHistory.subList(0, placeToSlice));

        recalculateChains();
    }

    private void recalculateChains() {
        this.activeChains = new ActiveChains(this);
        char[][] currBoard = getCurrentBoard();

        for (int row = 0; row < currBoard.length; row++) {
            for (int col = 0; col<currBoard[row].length; col++) {
                char currPiece = currBoard[row][col];
                BoardCoordinates currPosition = new BoardCoordinates(row,col);

                if (Pieces.isAPlayer(currPiece) && activeChains.hasNoChainWithPiece(currPosition)) {
                    activeChains.addNewChainAndUpdateCurrChains(currPosition, Pieces.getPieceOwner(currPiece));
                }
            }
        }
    }

    HashMap<Pieces, Integer> getCurrentScore() {
        this.activeChains.clearTerritories();

        char[][] currBoard = getCurrentBoard();

        for (int row = 0; row < currBoard.length; row++) {
            for (int col = 0; col<currBoard[row].length; col++) {
                char currPiece = currBoard[row][col];
                BoardCoordinates currPosition = new BoardCoordinates(row,col);

                if (Pieces.isASpace(currPiece) && activeChains.hasNoTerritoryWithSpace(currPosition)) {
                    activeChains.addTerritory(currPosition);
                }

            }
        }

        HashMap<Pieces, Integer> numbStones = countNumbStonesOnBoard();
        HashMap<Pieces, Integer> territoryScore = this.activeChains.getTerritoryScore();

        territoryScore.forEach((key, val) -> numbStones.merge(key, val, Integer::sum));
        return numbStones;
    }

    private HashMap<Pieces, Integer> countNumbStonesOnBoard() {
        char[][] currBoard = getCurrentBoard();
        HashMap<Pieces, Integer> numbPieces = new HashMap<Pieces, Integer>() {{put(Pieces.black, 0); put(Pieces.white, 0);}};

        for (int row = 0; row < currBoard.length; row++) {
            for (int col = 0; col<currBoard[row].length; col++) {
                char c = currBoard[row][col];

                if (Pieces.isAPlayer(c)) {
                    numbPieces.compute(Pieces.getPieceOwner(c), (key, val) -> val+1);
                }

            }
        }

        return numbPieces;
    }

    boolean bothPlayersPassed() {
        if (boardHistory.size() < 3) return false;

        char[][] mostRecentBoard = getCurrentBoard();
        char[][] previousTurn = this.boardHistory.get(this.boardHistory.size() - 2);
        char[][] twoTurnsAgo = this.boardHistory.get(this.boardHistory.size() - 3);

        return Arrays.deepEquals(mostRecentBoard, previousTurn) && Arrays.deepEquals(previousTurn, twoTurnsAgo);
    }

    boolean hasNoEmptySpaces() {
        char[][] mostRecentBoard = getCurrentBoard();

        for (int i = 0; i<mostRecentBoard.length; i++) {
            for (int j = 0; j<mostRecentBoard[i].length; j++) {
                if (mostRecentBoard[i][j] == Pieces.neutral.stone) {
                    return false;
                }
            }
        }

        return true;
    }
}
