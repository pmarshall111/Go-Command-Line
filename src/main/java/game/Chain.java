package game;

import java.util.HashSet;

class Chain {
    private final Pieces player;
    private final HashSet<BoardCoordinates> piecesInChain;
    private final HashSet<BoardCoordinates> liberties;
    final HashSet<BoardCoordinates> blackSurrounds;
    final HashSet<BoardCoordinates> whiteSurrounds;

    private HashSet<BoardCoordinates> newPieces;

    Chain(Pieces player) {
        this.player = player;

        this.piecesInChain = new HashSet<>();
        this.liberties = new HashSet<>();
        this.blackSurrounds = new HashSet<>();
        this.whiteSurrounds = new HashSet<>();
    }

    boolean isNextTo(BoardCoordinates pos) {
        return liberties.contains(pos);
    }

    boolean hasPieceInChain(BoardCoordinates pos) {
        return piecesInChain.contains(pos);
    }

    boolean hasNoLiberties() {
        return this.liberties.size() == 0;
    }

    void addFriendlyPiece(BoardCoordinates newPiece) {
        this.piecesInChain.add(newPiece);
        this.liberties.remove(newPiece);
    }

    void addEnemyPiece(BoardCoordinates newPiece, Pieces pieceOwnedBy) {
        if (pieceOwnedBy.equals(player)) {
            throw new IllegalArgumentException("Cannot add an enemy piece when it is owned by us.");
        } else if (Pieces.black.equals(pieceOwnedBy)) {
            this.blackSurrounds.add(newPiece);
        } else if (Pieces.white.equals(pieceOwnedBy)) {
            this.whiteSurrounds.add(newPiece);
        }
        this.liberties.remove(newPiece);
    }

    void joinChains(Chain chain) {
        for (BoardCoordinates coords: piecesInChain) {
            if (chain.hasPieceInChain(coords)) {
                combineStonesAndLiberties(chain);
                return;
            }
        }
        throw new RuntimeException("Tried to join chains when chains do not contain any duplicate stones");
    }

    private void combineStonesAndLiberties(Chain tributary) {
        this.blackSurrounds.addAll(tributary.getBlackSurrounds());
        this.whiteSurrounds.addAll(tributary.getWhiteSurrounds());
        this.piecesInChain.addAll(tributary.getPiecesInChain());
        this.liberties.addAll(tributary.getLiberties());
    }

    void changeEnemiesToSpaces(HashSet<BoardCoordinates> capturedPieces) {
        for (BoardCoordinates captured: capturedPieces) {
            if (pieceIsEnemyNeighbour(captured)) {
                this.blackSurrounds.remove(captured);
                this.whiteSurrounds.remove(captured);

                this.liberties.add(captured);
            }
        }
    }

    private boolean pieceIsEnemyNeighbour(BoardCoordinates pos) {
        return this.blackSurrounds.contains(pos) || this.whiteSurrounds.contains(pos);
    }


    public HashSet<BoardCoordinates> getPiecesInChain() {
        return new HashSet<>(piecesInChain);
    }

    HashSet<BoardCoordinates> getLiberties() {
        return new HashSet<>(liberties);
    }

    private HashSet<BoardCoordinates> getBlackSurrounds() {
        return new HashSet<>(blackSurrounds);
    }

    public HashSet<BoardCoordinates> getWhiteSurrounds() {
        return whiteSurrounds;
    }

    boolean isOwnedBy(Pieces player) {
        return this.player.equals(player);
    }


    private void getNeighbours(Board board, HashSet<BoardCoordinates> newPiecesInChain, HashSet<BoardCoordinates> capturedPiecesToBeRemoved) {
        //unable to add pieces to a collection while looping so need to use 2 sets and combine.
        piecesInChain.addAll(newPiecesInChain);
        newPieces = new HashSet<>();

        for (BoardCoordinates stone: newPiecesInChain) {
            findNeighboursForStone(board, capturedPiecesToBeRemoved, stone);
        }

        if (newPieces.size() > 0) {
            getNeighbours(board, newPieces, capturedPiecesToBeRemoved);
        }
    }
    void findNeighboursWhenNew(Board board, HashSet<BoardCoordinates> capturedPieces) {
        getNeighbours(board, this.piecesInChain, capturedPieces);
    }

    void findNeighboursForStone(Board board, HashSet<BoardCoordinates> capturedPiecesToBeRemoved, BoardCoordinates boardCoordinates) {
        BoardCoordinates[] surroundings = getSurroundings(boardCoordinates);

        for (BoardCoordinates neighbour: surroundings) {

            Character character = board.getPiece(neighbour);
            if (character == null) continue;
            else if (foundUnseenFriendlyStone(character, neighbour)) {
                newPieces.add(neighbour);
            } else if (character == Pieces.neutral.stone || capturedPiecesToBeRemoved.contains(neighbour)) {
                liberties.add(neighbour);
            } else if (character == Pieces.black.stone) {
                blackSurrounds.add(neighbour);
            } else if (character == Pieces.white.stone) {
                whiteSurrounds.add(neighbour);
            }
        }
    }

    private boolean foundUnseenFriendlyStone(Character c, BoardCoordinates pos) {
        return c == player.stone && !newPieces.contains(pos) && !piecesInChain.contains(pos);
    }

    private BoardCoordinates[] getSurroundings(BoardCoordinates pos) {
        BoardCoordinates up = new BoardCoordinates(pos.y-1, pos.x);
        BoardCoordinates down = new BoardCoordinates(pos.y+1, pos.x);
        BoardCoordinates left = new BoardCoordinates(pos.y, pos.x-1);
        BoardCoordinates right = new BoardCoordinates(pos.y, pos.x+1);

        return new BoardCoordinates[]{up, down, left, right};
    }

}
