import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PlayerTest {

  public static class CellAndBombs {
    Player.APlayer me;
    Player.APlayer opponent;
    
    @Before
    public void setup() {
      me = new Player.APlayer(0, 0,0,0,0);
      opponent = new Player.APlayer(1,12,10,0,0);
    }
    
    @Test
    public void copyState() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      game.currentState.players[0] = me;
      game.currentState.players[0] = opponent;
      
      game.currentState.addRow(0, ".....");
      game.currentState.addRow(1, ".X.X.");
      game.currentState.addRow(2, ".....");
      game.currentState.addRow(3, ".X.X."); 
      game.currentState.addRow(4, "....."); 
      
      Player.Bomb bomb = new Player.Bomb(0, 0, 0, 2, 1);
      game.currentState.addEntity(bomb);
      
      game.currentState.computeRound();
      game.updateNextStates();
      
      assertThat(game.currentState.grid[0][0].explodedBombs.isEmpty(), is(true));

      assertThat(game.states[1].grid[0][0].explodedBombs.contains(bomb), is(true));
    }
  }
}
