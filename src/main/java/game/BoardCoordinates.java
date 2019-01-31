package game;


import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//numbers are y axis, letters are x axis.
//numbers start from the bottom and go up so we need to flip them.
class BoardCoordinates {
    final int y;
    final int x;

    public BoardCoordinates(int y, int x) {
        this.y = y;
        this.x = x;
    }
    public BoardCoordinates(String position, int boardHeight) {
        Pattern numberP = Pattern.compile("\\d+");
        Matcher numberM = numberP.matcher(position);
        int yPos = -1;

        while (numberM.find()) {
            int digit = Integer.parseInt(numberM.group());

            yPos = boardHeight - digit; //accounting for the y axis of the board being flipped.
        }

        Pattern letterP = Pattern.compile("[A-Za-z]+");
        Matcher letterM = letterP.matcher(position);
        int xPos = -1;

        while (letterM.find()) {
            String letter = letterM.group().toUpperCase();
            xPos = Go.xAxisLabels.indexOf(letter);
        }

        if (xPos == -1 || yPos == -1) throw new IllegalArgumentException("Could not get a coordinate from user input");
        else {
            this.y = yPos;
            this.x = xPos;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardCoordinates that = (BoardCoordinates) o;
        return y == that.y &&
                x == that.x;
    }

    @Override
    public int hashCode() {
        return Objects.hash(y, x);
    }
}
