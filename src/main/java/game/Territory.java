package game;

public class Territory extends Chain {
    public Territory(Pieces player) {
        super(player);
    }

    Pieces isSurroundedBy() {
        if (blackSurrounds.size() == 0 && whiteSurrounds.size() > 0) {
            return Pieces.white;
        } else if (whiteSurrounds.size() == 0 && blackSurrounds.size() > 0) {
            return Pieces.black;
        } else return Pieces.neutral;
    }
}
