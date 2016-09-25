import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class PlayerTest {

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
    public void willExplode() throws Exception {
      Player.Cell[][] grid = createGrid( 
          "...."
          );
      grid[2][0].willExplode = 2;
      
      Player.Path p = new Player.Path(grid, 0, 0, 3, 0);
      Player.Path.PathItem pathItem = p.find();
      
      Player.Path.PathItem i = pathItem;
      int count = i.length()-1;
      while (i != null) {
        if (i.cell.willExplode == count) {
          pathItem = null;
          break;
        }
        count--;
        i = i.precedent;
      }
      
      
      assertThat(pathItem == null, is(true));
    }
    
    private Player.Cell[][] createGrid(String...values) {
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

}
