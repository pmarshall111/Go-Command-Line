import game.Go;
import game.Pieces;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

//NOTE: project can be pasted into Codewars Go challenge to take on more tests.
public class GoTest {
    private Go goSize5;
    private Go goSize7;
    private Go goSize9;

    @Before
    public void setUp() throws Exception {
        goSize5 = new Go(5);
        goSize5.move("3C", "4C", "4D", "3B", "4B", "2C", "5C", "3D", "4C", "2E", "3A", "2B", "4A", "2A", "4E", "3E");


        goSize7 = new Go(7);
        goSize7.move("5C", "4C", "4D", "3D", "3C", "4B", "3B", "5D", "4E", "5B", "3E", "6C", "2D", "5E", "5F",
                "6F", "4F", "6G", "5G", "3A", "2A", "4A", "2B");

        goSize9 = new Go(9);
        goSize9.move("9D", "9H", "9G", "8H", "8G", "7G", "8F", "6J", "8D", "6H", "7F", "6G", "7E", "6F", "7D", "6E", "7C", "5J", "7B", "5E", "7A", "5D", "6C", "5C",
                "5H", "4E", "5G", "4B", "5F", "3E", "5B", "3D", "5A", "3C", "4H", "3B", "4F", "3A", "3J", "2C", "3H", "1C", "3G");
        String[] movesLeft = new String[]{"2G", "2F", "2E", "2D", "1G", "1D"};
        for (String move: movesLeft) {
            goSize9.passTurn();
            goSize9.move(move);
        }
    }

    @Test
    public void testGettingBoard() {
        char[][] board = goSize5.getBoard();
        
        char[][] expected = new char[][]{
                {'.','.','x','.','.'},
                 {'x','x','x','x','x'},
                 {'x','o','x','o','o'},
                 {'o','o','o','.','o'},
                 {'.','.','.','.','.'}
        };

        Assert.assertTrue("Boards the same length", board.length == expected.length);
        for (int i = 0; i<expected.length; i++) {
            Assert.assertArrayEquals(expected[i], board[i]);
        }
    }

    @Test
    public void testGettingBoard2() {
        char[][] board = goSize7.getBoard();

        char[][] expected = new char[][]{
                {'.','.','.','.','.','.','.'},
                {'.','.','o','.','.','o','o'},
                {'.','o','.','o','o','x','x'},
                {'o','o','o','x','x','x','.'},
                {'o','x','x','.','x','.','.'},
                {'x','x','.','x','.','.','.'},
                {'.','.','.','.','.','.','.'}
        };

        Assert.assertTrue("Boards the same length", board.length == expected.length);
        for (int i = 0; i<expected.length; i++) {
            Assert.assertArrayEquals(expected[i], board[i]);
        }
    }

    @Test
    public void testGettingBoard3() {
        char[][] board = goSize9.getBoard();

        char[][] expected = new char[][]{
                {'.','.','.','x','.','.','x','o', '.'},
                {'.','.','.','x','.','x','x','o', '.'},
                {'x','x','x','x','x','x','o','.', '.'},
                {'.','.','x','.','o','o','o','o', 'o'},
                {'x','x','o','o','o','x','x','x', 'o'},
                {'.','o','.','.','o','x','.','x', '.'},
                {'o','o','o','o','o','.','x','x', 'x'},
                {'.','.','o','x','x','x','x','.', '.'},
                {'.','.','o','x','.','.','x','.', '.'},
        };

        Assert.assertTrue("Boards the same length", board.length == expected.length);
        for (int i = 0; i<expected.length; i++) {
            Assert.assertArrayEquals(expected[i], board[i]);
        }
    }

    @Test
    public void testRollBack() {
        char beforeRollBack = goSize5.getPosition("3E");
        goSize5.rollBack(1);
        char afterRollback = goSize5.getPosition("3E");
        Assert.assertTrue("Move taken away. Pos is now empty space", '.' == afterRollback);
        goSize5.move("3E");
        Assert.assertTrue("Rollback allows the player to put their stone somewhere else. Does not switch player",
                beforeRollBack == goSize5.getPosition("3E"));
    }

    @Test
    public void testPlayerChangeOnPass() {
        Pieces player1 = goSize5.getCurrPlayer();
        goSize5.move("2D");
        Pieces player2 = goSize5.getCurrPlayer();

        Assert.assertTrue("Player changes after a move", player1.stone != player2.stone);

        goSize5.passTurn();
        Assert.assertTrue("It is now first players turn again.", player1.stone == goSize5.getCurrPlayer().stone);
    }

    @Test
    public void testGetScores() {
        int[] scores = goSize5.getScores();

        int player1 = scores[0];
        int player2 = scores[1];

        Assert.assertTrue("Scores for size 5 game match up to Cosumi calculator", player1-player2 == -1);
    }

    @Test
    public void testGetScores2() {
        int[] scores = goSize7.getScores();

        int player1 = scores[0];
        int player2 = scores[1];

        Assert.assertTrue("Scores for size 7 game match up to Cosumi calculator", player1-player2 == 3);
    }

    public void testGetScores3() {
        int[] scores = goSize9.getScores();

        int player1 = scores[0];
        int player2 = scores[1];

        System.out.println(player1 + " " + player2);
        Assert.assertTrue("Scores for size 9 game match up", player1-player2 == 15);
    }

    @Test
    public void testGameStatus() {
        Assert.assertTrue("Game has not ended", !goSize7.gameHasEnded());
    }

    @Test
    public void testDoublePassEndsGame() {
        goSize7.passTurn();
        goSize7.passTurn();

        Assert.assertTrue("Game has now ended", goSize7.gameHasEnded());
    }

    @Test
    public void testHandicapPlacement() {
        Go go = new Go(9);
        go.handicapStones(5);

        char pos1 = go.getPosition("7C");
        char pos2 = go.getPosition("3G");
        char pos3 = go.getPosition("7G");
        char pos4 = go.getPosition("3C");
        char pos5 = go.getPosition("5E");

        Assert.assertTrue("Stones have been placed in all positions for size 9.",
                pos1 == pos2 && pos2 == pos3 && pos3 == pos4 && pos4 == pos5 && pos5 == 'x');
    }

    @Test
    public void testExceptionThrownTooManyHandicaps() {
        Go go = new Go(9);

        try {
            go.handicapStones(7);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }
}