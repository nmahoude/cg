import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PlayerTest {

  public static class CellAndBomb {
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
    public void placeBombOnCell() throws Exception {
      Player.Game game = new Player.Game(7, 1);
      game.grid = createGrid( 
          "......."
          );
      Player.Cell cell = game.grid[0][0];
      Player.Bomb bomb = new Player.Bomb(me, cell, 5, 3);
      
      cell.placeBomb(bomb);
    }
  }
  
  
  public static class PathFindingTests {
    @Test
    public void first() throws Exception {
      Player.Cell[][] grid = createGrid( 
          "..",
          ".."
          );
      
      Player.Path p = new Player.Path(grid, 0, 0, 1, 1);
      Player.Path.PathItem pathItem = p.find();
      assertThat(pathItem.cumulativeLength, is(2));
    }

    @Test
    public void blocked() throws Exception {
      Player.Cell[][] grid = createGrid( 
          "..0",
          ".X.",
          "0.."
          );
      
      Player.Path p = new Player.Path(grid, 0, 0, 2, 2);
      Player.Path.PathItem pathItem = p.find();
      assertThat(pathItem == null, is(true));
    }
   
    @Test
    public void willExplodeLinear() throws Exception {
      Player.Cell[][] grid = createGrid( 
          "...."
          );
      grid[2][0].willExplodeIn = 2;
      
      Player.Path p = new Player.Path(grid, 0, 0, 3, 0);
      Player.Path.PathItem pathItem = p.find();
      
      assertThat(pathItem == null, is(true));
    }
    
    @Test
    public void willExplodeOrthogonally() throws Exception {
      Player.Cell[][] grid = createGrid( 
          "X0X.",
          ".0..",
          "X.X.",
          "....",
          "X0X."
          );
      grid[1][3].willExplodeIn = 1;
      
      Player.Path p = new Player.Path(grid, 2, 3, 0, 3);
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
      game.grid = createGrid( 
          "......."
          );
      
      Player.Bomb b1 = new Player.Bomb(me, game.grid[0][0], 1, 3);
      game.updateBombInfluence(b1);

      assertThat(game.grid[0][0].willExplodeIn, is(1));
      assertThat(game.grid[1][0].willExplodeIn, is(1));
      assertThat(game.grid[2][0].willExplodeIn, is(1));
      assertThat(game.grid[3][0].willExplodeIn, is(1));

    }
    
    @Test
    public void ExplosionTriggersBeOldBomb() {
      Player.Game game = new Player.Game(7, 1);
      game.grid = createGrid( 
          "......."
          );
      
      Player.Bomb b1 = new Player.Bomb(me, game.grid[0][0], 1, 3);
      Player.Bomb b2 = new Player.Bomb(me, game.grid[3][0], 8, 3);
      
      game.updateBombInfluence(b1);
      game.updateBombInfluence(b2);
      
      assertThat(game.grid[6][0].willExplodeIn, is(1));
    }

    @Test
    public void ExplosionTriggersRetroactif() {
      Player.Game game = new Player.Game(7, 1);
      game.grid = createGrid( 
          "......."
          );
      
      Player.Bomb b1 = new Player.Bomb(me, game.grid[3][0], 1, 3);
      Player.Bomb b2 = new Player.Bomb(me, game.grid[0][0], 8, 3);
      
      game.updateBombInfluence( b2);
      game.updateBombInfluence(b1);
      
      assertThat(game.grid[6][0].willExplodeIn, is(1));
    }
    
    @Test
    public void ExplosionTriggers_3Bombs() {
      Player.Game game = new Player.Game(7, 1);
      game.grid = createGrid( 
          "......."
          );
      
      Player.Bomb b1 = new Player.Bomb(me, game.grid[0][0],  1, 3);
      Player.Bomb b2 = new Player.Bomb(me, game.grid[1][0], 2, 3);
      Player.Bomb b3 = new Player.Bomb(me, game.grid[2][0], 3, 3);
      
      game.updateBombInfluence(b1);
      game.updateBombInfluence(b2);
      game.updateBombInfluence(b3);
      
      assertThat(game.grid[5][0].willExplodeIn, is(1));
    }

    @Test
    public void myBombsDontOvershadowEnemiesBombs() throws Exception {
      Player.Game game = new Player.Game(7, 1);
      game.grid = createGrid( 
          "......."
          );
      
      Player.Bomb b1 = new Player.Bomb(me, game.grid[0][0], 1, 3);
      game.updateBombInfluence(b1);

      Player.Bomb b2 = new Player.Bomb(opponent, game.grid[6][0], 1, 6);
      game.updateBombInfluence(b2);

      assertThat(game.grid[0][0].isSafe(me, 0), is(false));
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
