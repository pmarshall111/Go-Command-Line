package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

class ActiveChains {
    private final Board parentBoard;
    private HashSet<Chain> chains;
    private HashSet<Territory> territories;

    private Chain chainExtendedByMove = null;
    private HashSet<BoardCoordinates> capturedStones = new HashSet<>();

    ActiveChains(Board board) {
        chains = new HashSet<>();
        territories = new HashSet<>();
        parentBoard = board;
    }

    //i don't like this process of adding a new chain.
    void addMoveToChains(BoardCoordinates movePosition, Pieces currPlayer) {
        addMoveToChains(movePosition, currPlayer, null);
    }
    private void addMoveToChains(BoardCoordinates movePosition, Pieces currPlayer, Chain newlyCreatedChain) {
        chainExtendedByMove = newlyCreatedChain;
        capturedStones = new HashSet<>();

        for (Chain chain : new ArrayList<>(chains)) {
            if (chain.isNextTo(movePosition)) {

                if (chain.isOwnedBy(currPlayer)) {
                    addFriendlyPiece(chain, movePosition);
                } else {
                    addEnemyPiece(chain, movePosition, currPlayer);
                }
            }
        }

        if (chainExtendedByMove == null) {
            addNewChain(movePosition, currPlayer);
        }

        if (capturedStones.size() > 0) {
            addSpacesAroundChains(currPlayer);
        }
    }

    HashSet<BoardCoordinates> getCapturedStones() {
        return new HashSet<>(capturedStones);
    }

    private void addSpacesAroundChains(Pieces currPlayer) {
        for (Chain chain : chains) {

            if (chain.isOwnedBy(currPlayer)) {
                chain.changeEnemiesToSpaces(capturedStones);
            }

        }
    }

    private void addNewChain(BoardCoordinates movePosition, Pieces currPlayer) {
        createChain(movePosition, currPlayer, false);
    }

    void addNewChainAndUpdateCurrChains(BoardCoordinates movePosition, Pieces currPlayer) {
        createChain(movePosition, currPlayer, true);
    }

    private void createChain(BoardCoordinates movePosition, Pieces currPlayer, boolean needToUpdateCurrentChains) {
        Chain newChain = new Chain(currPlayer);
        newChain.addFriendlyPiece(movePosition);

        if (needToUpdateCurrentChains) {
            addMoveToChains(movePosition, currPlayer, newChain);
        }

        newChain.findNeighboursWhenNew(parentBoard, capturedStones);
        this.chains.add(newChain);
    }

    private void addFriendlyPiece(Chain chain, BoardCoordinates movePosition) {
        chain.addFriendlyPiece(movePosition);
        chain.findNeighboursForStone(parentBoard, capturedStones, movePosition);

        if (this.chainExtendedByMove != null) {
            mergeChains(chain);
        }

        this.chainExtendedByMove = chain;
    }

    private void mergeChains(Chain nowMainChain) {
        nowMainChain.joinChains(chainExtendedByMove);
        chains.remove(chainExtendedByMove);
    }

    private void addEnemyPiece(Chain chain, BoardCoordinates movePosition, Pieces currPlayer) {
        chain.addEnemyPiece(movePosition, currPlayer);

        if (chain.hasNoLiberties()) {
            Set<BoardCoordinates> capturedPieces = chain.getPiecesInChain();
            this.capturedStones.addAll(capturedPieces);

            this.chains.remove(chain);
        }
    }

    boolean hasNoChainWithPiece(BoardCoordinates movePosition) {
        for (Chain c: chains) {
            if (c.hasPieceInChain(movePosition)) {
                return false;
            }
        }
        return true;
    }

    boolean hasChainWithNoLiberties() {
        for (Chain chain : chains) {
            if (chain.hasNoLiberties()) {
                return true;
            }
        }
        return false;
    }



    void clearTerritories() {
        this.territories.clear();
    }

    boolean hasNoTerritoryWithSpace(BoardCoordinates piecePosition) {
        for (Chain c: this.territories) {
            if (c.hasPieceInChain(piecePosition)) return false;
        }
        return true;
    }

    void addTerritory(BoardCoordinates piecePosition) {
        Territory newTerritory = new Territory(Pieces.neutral);
        newTerritory.addFriendlyPiece(piecePosition);

        newTerritory.findNeighboursWhenNew(parentBoard, new HashSet<>());
        territories.add(newTerritory);
    }

    HashMap<Pieces, Integer> getTerritoryScore() {
        HashMap<Pieces, Integer> numbPieces = new HashMap<Pieces, Integer>() {{put(Pieces.black, 0); put(Pieces.white, 0); put(Pieces.neutral, 0);}};

        for (Territory t: territories) {
            int piecesInTerrtory = t.getPiecesInChain().size();
            numbPieces.compute(t.isSurroundedBy(), (key, val) -> val + piecesInTerrtory);
        }

        return numbPieces;
    }
}
