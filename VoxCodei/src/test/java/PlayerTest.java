import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class PlayerTest {
  Player player;
  
  @Before
  public void setup() {
  }
  @Test
  public void placeBomb() {
    player = new Player(2,2,Arrays.asList("..", ".."));
    
    player.grid.placeBomb(0, 0);
    
    assertThat(player.grid.getRow(0), is("3."));
    assertThat(player.grid.getRow(1), is(".."));
  }
}
