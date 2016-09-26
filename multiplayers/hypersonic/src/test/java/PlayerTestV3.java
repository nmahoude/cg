import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PlayerTestV3 {

  public static class CellAndBombs {
    Player.APlayer me;
    Player.APlayer opponent;
    
    @Before
    public void setup() {
      me = new Player.APlayer();
      me.index = 0;
      opponent = new Player.APlayer();
      opponent.index = 1;
    }
    
    @Test
    public void copyState() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      game.addRow(0, ".....");
      game.addRow(1, ".X.X.");
      game.addRow(2, ".....");
      game.addRow(3, ".X.X."); 
      game.addRow(4, "....."); 
      
      Player.Bomb bomb = new Player.Bomb(me, game.states[0].grid[0][0], 1, 1);
      game.updateOneBomb(bomb);
      Player.Bomb bomb2 = new Player.Bomb(me, game.states[0].grid[2][2], 2, 1);
      game.updateOneBomb(bomb2);

      for (int i=0;i<2;i++) {
        game.simulateOneTurn(i);
        game.debugBombExplosions(""+i, i);
      }
      
      assertThat(game.states[1].grid[0][0].bomb == null, is(true));
      assertThat(game.states[1].grid[2][2].bomb == bomb2, is(true));

      assertThat(game.states[2].grid[0][0].bomb == null, is(true));
      assertThat(game.states[2].grid[2][2].bomb == null, is(true));
    }
    
    @Test
    public void boxDecrement() throws Exception {
      Player.Game game = new Player.Game(5, 5);
      game.addRow(0, ".0...");
      game.addRow(1, ".X.X.");
      game.addRow(2, ".....");
      game.addRow(3, ".X.X."); 
      game.addRow(4, "....."); 
      
      Player.Bomb bomb = new Player.Bomb(me, game.states[0].grid[0][0], 8, 1);
      game.updateOneBomb(bomb);

      for (int i=0;i<2;i++) {
        game.simulateOneTurn(i);
        //game.debugBombExplosions(""+i, i);
      }
      
      assertThat(game.states[0].grid[1][0].willExplodeIn, is(8));
      assertThat(game.states[1].grid[1][0].willExplodeIn, is(7));
      assertThat(game.states[2].grid[1][0].willExplodeIn, is(6));
    }
    
    @Test
    public void temporalDestroy() throws Exception {
      Player.Game game = new Player.Game(4, 4);
      game.addRow(0, "....");
      game.addRow(1, "....");
      game.addRow(2, "....");
      game.addRow(3, "...."); 
      
      Player.Cell cell = game.states[0].grid[0][0];
      Player.Bomb bomb = new Player.Bomb(me, cell, 1, 1);
      game.updateOneBomb(bomb);

      game.simulateOneTurn(0);
      
      assertThat(cell.bomb, is(bomb));
      assertThat(game.states[0].grid[0][0].isBlocked(), is(true));
      assertThat(game.states[0].grid[1][0].isBlocked(), is(true));
      assertThat(game.states[0].grid[0][1].isBlocked(), is(true));
      assertThat(game.states[0].grid[2][0].isBlocked(), is(false));
      assertThat(game.states[0].grid[0][2].isBlocked(), is(false));
    }

    @Test
    public void triggerDestroy() throws Exception {
      Player.Game game = new Player.Game(4, 4);
      game.addRow(0, "....");
      game.addRow(1, "....");
      game.addRow(2, "....");
      game.addRow(3, "...."); 
      
      Player.Bomb bomb1 = new Player.Bomb(me, game.states[0].grid[0][0], 1, 1);
      Player.Bomb bomb2 = new Player.Bomb(me, game.states[0].grid[1][0], 8, 1);
      game.updateOneBomb(bomb1);
      game.updateOneBomb(bomb2);

      game.simulateOneTurn(0);
      
      assertThat(game.states[0].grid[1][1].explodingBombs.contains(bomb2), is(true));
      assertThat(game.states[0].grid[1][1].isBlocked(), is(true));
    }

    @Test
    public void placeBombOnCell() throws Exception {
      Player.Game game = new Player.Game(7, 1);
      game.addRow(0, ".......");
      Player.Cell cell = game.states[0].grid[0][0];
      Player.Bomb bomb = new Player.Bomb(me, cell, 1, 3);
      
      game.updateOneBomb(bomb);
      game.simulateOneTurn(0);
      
      assertThat(cell.bomb, is(bomb));
      assertThat(game.states[0].grid[3][0].explodingBombs.contains(bomb), is(true));
      assertThat(game.states[0].grid[4][0].explodingBombs.contains(bomb), is(false));
    }
    @Test
    public void placeBombOnCellInfluencedBlockedByWall() throws Exception {
      Player.Game game = new Player.Game(7, 1);
      game.addRow(0, ".X.....");
      Player.Cell cell = game.states[0].grid[0][0];
      Player.Bomb bomb = new Player.Bomb(me, cell, 5, 3);
      
      game.updateOneBomb(bomb);
      game.simulateOneTurn(0);
      
      assertThat(cell.bomb, is(bomb));
      assertThat(game.states[0].grid[1][0].explodingBombs.contains(bomb), is(false));
      assertThat(game.states[0].grid[2][0].explodingBombs.contains(bomb), is(false));
    }
    
    @Test
    public void placeBombOnCellBlockedByBox() throws Exception {
      Player.Game game = new Player.Game(7, 1);
      game.addRow(0, ".0.....");
      Player.Cell cell = game.states[0].grid[0][0];
      Player.Bomb bomb = new Player.Bomb(me, cell, 1, 3);
      
      game.updateOneBomb(bomb);
      game.simulateOneTurn(0);
      
      assertThat(cell.bomb, is(bomb));
      assertThat(game.states[0].grid[1][0].explodingBombs.contains(bomb), is(true));
      assertThat(game.states[0].grid[2][0].explodingBombs.contains(bomb), is(false));
    }
  }
  
  
  public static class PathFindingTests {
    @Test
    public void first() throws Exception {
      Player.Game game = new Player.Game(7, 1);
      game.addRow(0, ".......");

      for (int i=0;i<8;i++)
        game.simulateOneTurn(i);

      
      Player.Path p = new Player.Path(game.states, 0, 0, 2, 0);
      Player.Path.PathItem pathItem = p.find();
      assertThat(pathItem.cumulativeLength, is(2));
    }

    @Test
    public void blocked() throws Exception {
      Player.Game game = new Player.Game(3, 3);
      game.addRow(0, "..X");
      game.addRow(1, ".X.");
      game.addRow(2, "X..");

      for (int i=0;i<8;i++)
        game.simulateOneTurn(i);

      
      Player.Path p = new Player.Path(game.states, 0, 0, 2, 2);
      Player.Path.PathItem pathItem = p.find();
      assertThat(pathItem == null, is(true));
    }
  }
  public static class ExplosionTriggers {
    Player.APlayer me;
    Player.APlayer opponent;
    
    @Before
    public void setup() {
      me = new Player.APlayer();
      me.index = 0;
      opponent = new Player.APlayer();
      opponent.index = 1;
    }
    
    @Test
    public void range() throws Exception {
      Player.Game game = new Player.Game(7, 1);
      game.states[0].grid = createGrid( 
          "......."
          );
      
      Player.Bomb b1 = new Player.Bomb(me, game.states[0].grid[0][0], 1, 3);
      game.updateBombInfluence(b1);

      assertThat(game.states[0].grid[0][0].willExplodeIn, is(1));
      assertThat(game.states[0].grid[1][0].willExplodeIn, is(1));
      assertThat(game.states[0].grid[2][0].willExplodeIn, is(1));
      assertThat(game.states[0].grid[3][0].willExplodeIn, is(1));

    }
    
    @Test
    public void ExplosionTriggersBeOldBomb() {
      Player.Game game = new Player.Game(7, 1);
      game.states[0].grid = createGrid( 
          "......."
          );
      
      Player.Bomb b1 = new Player.Bomb(me, game.states[0].grid[0][0], 1, 3);
      Player.Bomb b2 = new Player.Bomb(me, game.states[0].grid[3][0], 8, 3);
      
      game.updateBombInfluence(b1);
      game.updateBombInfluence(b2);
      
      assertThat(game.states[0].grid[6][0].willExplodeIn, is(1));
    }

    @Test
    public void ExplosionTriggersRetroactif() {
      Player.Game game = new Player.Game(7, 1);
      game.states[0].grid = createGrid( 
          "......."
          );
      
      Player.Bomb b1 = new Player.Bomb(me, game.states[0].grid[3][0], 1, 3);
      Player.Bomb b2 = new Player.Bomb(me, game.states[0].grid[0][0], 8, 3);
      
      game.updateBombInfluence( b2);
      game.updateBombInfluence(b1);
      
      assertThat(game.states[0].grid[6][0].willExplodeIn, is(1));
    }
    
    @Test
    public void ExplosionTriggers_3Bombs() {
      Player.Game game = new Player.Game(7, 1);
      game.states[0].grid = createGrid( 
          "......."
          );
      
      Player.Bomb b1 = new Player.Bomb(me, game.states[0].grid[0][0],  1, 3);
      Player.Bomb b2 = new Player.Bomb(me, game.states[0].grid[1][0], 2, 3);
      Player.Bomb b3 = new Player.Bomb(me, game.states[0].grid[2][0], 3, 3);
      
      game.updateBombInfluence(b1);
      game.updateBombInfluence(b2);
      game.updateBombInfluence(b3);
      
      assertThat(game.states[0].grid[5][0].willExplodeIn, is(1));
    }

    @Test
    public void myBombsDontOvershadowEnemiesBombs() throws Exception {
      Player.Game game = new Player.Game(8, 1);
      game.addRow(0, "........");
      
      Player.Bomb b1 = new Player.Bomb(me, game.states[0].grid[0][0], 1, 3);
      Player.Bomb b2 = new Player.Bomb(opponent, game.states[0].grid[6][0], 1, 6);
      game.updateOneBomb(b1);
      game.updateOneBomb(b2);
      
      game.simulateOneTurn(0);

      assertThat(game.states[0].grid[0][0].isSafe(me), is(false));
    }
  }

  private static Player.Cell[][] createGrid(String...values) {
    int w = values[0].length();
    int h = values.length;
    Player.Cell[][] grid = new Player.Cell[w][h];
    
    for (int y=0;y<h;y++) {
      for (int x=0;x<w;x++) {
        char c = values[y].charAt(x);
        Player.Cell cell = new Player.Cell(x,y);
        if (c=='X') {
          cell.type = Player.Cell.Type.WALL;
        } else if (c=='0') {
          cell.type = Player.Cell.Type.BOX;
        } else {
          cell.type = Player.Cell.Type.FLOOR;
        }
        grid[x][y] = cell;
      }
    }
    return grid;
  }
}
