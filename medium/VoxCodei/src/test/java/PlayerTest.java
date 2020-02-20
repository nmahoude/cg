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
    player = new Player(2,2,Arrays.asList(
        "..", 
        ".."));
    
    player.grid.placeBomb(0, 0);
    
    assertThat(player.grid.getRow(0), is("3."));
    assertThat(player.grid.getRow(1), is(".."));
  }

  @Test
  public void update_decreaseBombTimer() {
    player = new Player(2,2,Arrays.asList(
        "..", 
        ".."));
    player.grid.placeBomb(0, 0);
    
    player.grid.udpate();
    
    assertThat(player.grid.getRow(0), is("2."));
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
    
    player.bombsLeft = 1;
    player.leftRounds = 1;
    
  }
  
  @Test
  public void cc_test1() {
    player = new Player(4,3,Arrays.asList(
        ".@..", 
        "....", 
        "...."));
    player.bombsLeft = 1;
    player.leftRounds = 15;
    
  }
  
  @Test
  public void oneBombFourSentinels() {
    player = new Player(4,3,Arrays.asList(
        ".@..", 
        "@.@.", 
        ".@.."));
    player.bombsLeft = 1;
    player.leftRounds = 4;
    
  }
  
  @Test
  public void oneBombSentinelsOnLine() {
    player = new Player(4,3,Arrays.asList(
        "....", 
        "@.@@", 
        "...."));
    player.bombsLeft = 3;
    player.leftRounds = 15;
    
  }
  
  @Test
  public void bigBoard() {
    player = new Player(12,9,Arrays.asList(
        "@...@.......",
        ".......@...@",
        "............",
        "...@.....@..",
        "............",
        ".@..........",
        "......@.....",
        ".........@..",
        "............"
        ));
    player.bombsLeft = 9;
    player.leftRounds = 14;
    
  }
  @Test
  public void undestrucctible() {
    player = new Player(12,9,Arrays.asList(
        "............",
        "..##....##..",
        ".#@@#..#@@#.",
        "............",
        ".#@@#..#@@#.",
        "..##....##..",
        "............",
        "............",
        "............"
        ));
    player.bombsLeft = 4;
    player.leftRounds = 15;
    
  }
  
  @Test
  public void forseeFuture() {
    player = new Player(5,5,Arrays.asList(
        ".....",
        "...@.",
        "2eee@",
        "...@.",
        "....."
        ));
    player.bombsLeft = 1;
    player.leftRounds = 15;
    
  }
  
  @Test
  public void betterForseeFuture() {
    player = new Player(8,5,Arrays.asList(
        "........",
        "......@.",
        "@@@.@@@@",
        "......@.",
        "........"
        ));
    player.bombsLeft = 2;
    player.leftRounds = 15;
    
  }
}
