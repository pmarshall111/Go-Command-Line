package game;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

enum PositionOfHandicapStones {
    size9 ( new BoardCoordinates(2,6),
            new BoardCoordinates(6,2),
            new BoardCoordinates(6,6),
            new BoardCoordinates(2,2),
            new BoardCoordinates(4,4)),

    size13 (new BoardCoordinates(3,9),
            new BoardCoordinates(9,3),
            new BoardCoordinates(9,9),
            new BoardCoordinates(3,3),
            new BoardCoordinates(6,6),
            new BoardCoordinates(6,3),
            new BoardCoordinates(6,9),
            new BoardCoordinates(3,6),
            new BoardCoordinates(9,6)),

    size19 (new BoardCoordinates(3,15),
            new BoardCoordinates(15,3),
            new BoardCoordinates(15,15),
            new BoardCoordinates(3,3),
            new BoardCoordinates(9,9),
            new BoardCoordinates(9,3),
            new BoardCoordinates(9,15),
            new BoardCoordinates(3,9),
            new BoardCoordinates(15,9));

    private final ArrayList<BoardCoordinates> handicapStonesPosition;

    PositionOfHandicapStones(BoardCoordinates... args) {
        this.handicapStonesPosition = new ArrayList<>();
        this.handicapStonesPosition.addAll(Arrays.asList(args));
    }

    public ArrayList<BoardCoordinates> getStonesToPlace(int numbStonesRequired) {
        if (numbStonesRequired > getMaxNumbHandicapStones()) {
            throw new IllegalArgumentException("Asking for more handicap stones than is allowed");
        }

        List<BoardCoordinates> sublist = this.handicapStonesPosition.subList(0, numbStonesRequired);
        return new ArrayList<>(sublist);
    }

    public int getMaxNumbHandicapStones() {
        return this.handicapStonesPosition.size();
    }

    static PositionOfHandicapStones getHandicapsData(int height, int width) {
        if (height != width) return null;

        switch (height) {
            case 9:
                return PositionOfHandicapStones.size9;
            case 13:
                return PositionOfHandicapStones.size13;
            case 19:
                return PositionOfHandicapStones.size19;
            default:
                return null;
        }
    }
}
