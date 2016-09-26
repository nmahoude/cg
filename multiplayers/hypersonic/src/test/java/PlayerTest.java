import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class PlayerTest {

  public static class CellAndBombs {
    Player.APlayer me;
    Player.APlayer opponent;
    
    @Before
    public void setup() {
    }
    
    @Test
    public void copyState() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new Player.APlayer(game.currentState, 0, 0,0,0,0);
      opponent = new Player.APlayer(game.currentState, 1,5,5,0,0);

      game.currentState.players[0] = me;
      game.currentState.players[1] = opponent;
      
      game.currentState.addRow(0, ".....");
      game.currentState.addRow(1, ".X.X.");
      game.currentState.addRow(2, ".....");
      game.currentState.addRow(3, ".X.X."); 
      game.currentState.addRow(4, "....."); 
      
      Player.Bomb bomb = new Player.Bomb(game.currentState, 0, 0, 0 ,2, 2);
      game.currentState.addEntity(bomb);
      
      game.currentState.computeRound();
      game.updateNextStates();
      
      assertThat(game.currentState.grid[0][0], is(Player.GameState.CELL_BOMB_0+1));
      assertThat(game.states[1].grid[0][0], is(Player.GameState.CELL_FIRE));
    }
    
    @Test
    public void chainExplosion() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new Player.APlayer(game.currentState, 0, 0,0,0,0);
      opponent = new Player.APlayer(game.currentState, 1,5,5,0,0);

      game.currentState.players[0] = me;
      game.currentState.players[1] = opponent;
      
      game.currentState.addRow(0, ".....");
      game.currentState.addRow(1, ".X.X.");
      game.currentState.addRow(2, ".....");
      game.currentState.addRow(3, ".X.X."); 
      game.currentState.addRow(4, "....."); 
      
      Player.Bomb bomb = new Player.Bomb(game.currentState, 0, 0, 0 ,1, 2);
      game.currentState.addEntity(bomb);
      Player.Bomb bomb2 = new Player.Bomb(game.currentState, 0, 1, 0 ,8, 2);
      game.currentState.addEntity(bomb2);
      
      
      game.currentState.computeRound();
      
      int bomb1Value = game.currentState.grid[0][0];
      int bomb2Value = game.currentState.grid[0][1];
      
      assertThat(bomb1Value, is(Player.GameState.CELL_FIRE));
      assertThat(bomb2Value, is(Player.GameState.CELL_FIRE));
    }
    
    @Test
    public void bombsTriggerFireWhichDisapearsNextRound() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new Player.APlayer(game.currentState, 0, 0,0,0,0);
      opponent = new Player.APlayer(game.currentState, 1,5,5,0,0);

      game.currentState.players[0] = me;
      game.currentState.players[1] = opponent;
      
      game.currentState.addRow(0, ".....");
      game.currentState.addRow(1, ".X.X.");
      game.currentState.addRow(2, ".....");
      game.currentState.addRow(3, ".X.X."); 
      game.currentState.addRow(4, "....."); 
      
      Player.Bomb bomb = new Player.Bomb(game.currentState, 0, 0, 0 ,1, 2);
      game.currentState.addEntity(bomb);
      
      game.currentState.computeRound();
      game.updateNextStates();
      
      int cellValue = game.currentState.grid[0][1];
      int cellValueNext = game.states[1].grid[0][1];
      
      assertThat(cellValue, is(Player.GameState.CELL_FIRE));
      assertThat(cellValueNext, is(Player.GameState.CELL_FLOOR));
    }
  }
  
  public static class PathFinding {
    Player.APlayer me;
    Player.APlayer opponent;
    
    @Before
    public void setup() {
    }
    
    @Test
    public void easy() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      me = new Player.APlayer(game.currentState, 0, 0,0,0,0);
      opponent = new Player.APlayer(game.currentState, 1,5,5,0,0);

      game.currentState.players[0] = me;
      game.currentState.players[1] = opponent;
      
      game.currentState.addRow(0, "..0..");
      game.currentState.addRow(1, ".X.X.");
      game.currentState.addRow(2, "0....");
      game.currentState.addRow(3, ".X.X."); 
      game.currentState.addRow(4, "....."); 
      
      game.currentState.computeRound();
      game.updateNextStates();

      // path finding
      Player.Path path = new Player.Path(game.states, Player.P.get(0, 0), Player.P.get(1, 0));
      path.find();
      
      assertThat(path.path.isEmpty(), is(false));
    }
  }
}
