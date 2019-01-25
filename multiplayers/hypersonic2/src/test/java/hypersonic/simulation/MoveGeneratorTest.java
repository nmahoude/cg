package hypersonic.simulation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import hypersonic.Move;
import hypersonic.Player;

public class MoveGeneratorTest {

  @Before
  public void setup() {
    Player.myId = 0;
  }

  @Test
  public void cornered() throws Exception {
    String input = "....0...0....\r\n" +
        ".X.X1X.X1X.X.\r\n" +
        "...2.212.2...\r\n" +
        "1X2X0X.X0X2X1\r\n" +
        "...001.100...\r\n" +
        "2X.X.X.X.X.X2\r\n" +
        "...001.100...\r\n" +
        "1X2X0X.X0X2X1\r\n" +
        "...2.212.2...\r\n" +
        ".X.X1X.X1X.X.\r\n" +
        "....0...0....\r\n" +
        "2\r\n" +
        "0 0 0 0 1 3\r\n" +
        "0 1 12 10 1 3";

    Scanner in = new Scanner(input);
    Player player = new Player(in);
    player.readGameState();

    MoveGenerator gen = new MoveGenerator(player.state);

    Move moves[] = new Move[16];
    int movesFE = gen.getPossibleMoves(moves);

    assertThat(movesFE, is(6));
  }
}
