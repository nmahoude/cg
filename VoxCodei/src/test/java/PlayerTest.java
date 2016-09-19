import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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
    player = new Player(2,2,Arrays.asList(
        "..", 
        ".."));
    
    player.grid.placeBomb(0, 0);
    
    assertThat(player.grid.getRow(0), is("3."));
    assertThat(player.grid.getRow(1), is(".."));
  }

  @Test
  public void explodeAll() throws Exception {
    player = new Player(7,7,Arrays.asList(
        "...@...", 
        "...@...", 
        "...@...", 
        "@@@.@@@", 
        "...@...", 
        "...@...", 
        "...@..."));
    
    player.grid.explode(3, 3);
    
    int i=0;
    assertThat(player.grid.getRow(i++), is("......."));
    assertThat(player.grid.getRow(i++), is("......."));
    assertThat(player.grid.getRow(i++), is("......."));
    assertThat(player.grid.getRow(i++), is("......."));
    assertThat(player.grid.getRow(i++), is("......."));
    assertThat(player.grid.getRow(i++), is("......."));
    assertThat(player.grid.getRow(i++), is("......."));
  }
  
  @Test
  public void simulateSimple() {
    player = new Player(2,2,Arrays.asList(
        "@.", 
        ".."));
    
    player.grid.placeBomb(0, 0);
    Player.Simulation s = new Player.Simulation();
    Player.SimulationStep ss = s.simulate(player);
    
    assertThat(ss.x, is(0));
    assertThat(ss.y, is(1));
  }
  
  @Test
  public void cc_test1() {
    player = new Player(4,3,Arrays.asList(
        "....", 
        ".@..", 
        "...."));
    
    player.grid.placeBomb(0, 0);
    Player.Simulation s = new Player.Simulation();
    Player.SimulationStep ss = s.simulate(player);
    
    assertThat(ss.x, is(0));
    assertThat(ss.y, is(1));
  }
}
