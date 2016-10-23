import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class PlayerTest {

  @Test
  public void tower_start() throws Exception {
    Player player = new Player(1, 100);
    player.setInitPos(0, 99);

    player.calculateNextAction(Player.UNKNOWN);

    assertThat(player.projectedPos.y, is(0));
    assertThat(player.rect, is(new Player.Rectangle(0, 0, 0, 99)));
  }
  
  @Test
  public void tower_second_cold() throws Exception {
    Player player = new Player(1, 100);
    player.setInitPos(0, 99);
    
    player.currentPos = new Player.P(0,0);
    player.lastPos = new Player.P(0,99);
    player.calculateNextAction(Player.COLDER);

    assertThat(player.projectedPos.y, is(74-1));
    assertThat(player.rect, is(new Player.Rectangle(0, 50, 0, 99)));
  }
  
  @Test
  public void tower_second_warm() throws Exception {
    Player player = new Player(1, 100);
    player.setInitPos(0, 99);
    
    player.currentPos = new Player.P(0,0);
    player.lastPos = new Player.P(0,99);
    player.calculateNextAction(Player.WARMER);

    assertThat(player.projectedPos.y, is(24));
    assertThat(player.rect, is(new Player.Rectangle(0, 0, 0, 49)));
  }
}
