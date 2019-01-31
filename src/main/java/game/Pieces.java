package game;

public enum Pieces {
    white('o', 2),
    black('x', 1),
    neutral('.', 0);

    final static Pieces[] PLAYERS = new Pieces[]{Pieces.white, Pieces.black};

    public final char stone;
    public final int playerNumb;

    Pieces(char stone, int numb) {
        this.stone = stone;
        this.playerNumb = numb;
    }

    public static int getNumbPlayers() {
        return PLAYERS.length;
    }

    static Pieces getCurrentPlayer(int idx) {
        return PLAYERS[idx];
    }

    static Pieces getPieceOwner(char piece) {
        if (Pieces.white.stone == piece) {
            return Pieces.white;
        } else if (Pieces.black.stone == piece) {
            return Pieces.black;
        } else
            return Pieces.neutral;
    }

    static boolean isAPlayer(char c) {
        return c == Pieces.black.stone || c == Pieces.white.stone;
    }
    static boolean isASpace(char c) {return c == Pieces.neutral.stone;}
}
