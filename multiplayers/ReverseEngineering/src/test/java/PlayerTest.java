import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class PlayerTest {

  @Test
  @Ignore
  public void test1() throws Exception {
    char[][]board = createBoard(
        "****************************",
        "****************************",
        "*############***************",
        "#            #*            *",
        "# #### ##### #* ***** **** *",
        "# #*** ****# #* ***** **** *",
        "# #*** **### #* ***** **** *",
        "#                          *",
        "# #*** *# ###***** ** ******",
        "# ####2*# ###***** ** ******",
        "#   3 @## 5  #*    ** ******",
        "*##### **### #* ***** ******",
        "****** **### #* ***** ******",
        "****** *#       ***** ******",
        "****** *# ###* ****** ******",
        "****** *# #      **** ******",
        "****** *# #********** ******",
        "****** *# #      **** ******",
        "****** *# #** ******* ******",
        "****** *# 4        ** ******",
        "****** *# #******* ** ******",
        "*##### ## ###***** ** ******",
        "#            #*            *",
        "# #### ##### #* ***** **** *",
        "# ##*# ##### #* ***** **** *",
        "#   ##        *       **   *",
        "*## ## ## ####******* ** ***",
        "*## ## ## ###******** ** ***",
        "#      ##    #*******    ***",
        "# #####**### #**************",
        "# ########## #**************",
        "#            ***************",
        "*############***************",
        "****************************",
        "****************************"
        );
      Player.board = board;
      
      List<Player.P> bestPath = Player.findClosestUnexploredCell(new Player.P(5, 10));
      
      assertThat(bestPath, is(not(nullValue())));
      assertThat(bestPath.size(), is(not(0)));
  }

  private char[][] createBoard(String... rows) {
    Player.ghosts = new Player.Ghost[5];
    Player.pacman = new Player.Pacman();
    
    for(int g=0;g<Player.ghosts.length;g++) {
      Player.ghosts[g] = new Player.Ghost();
    }
    
    char[][] board = new char[28][35];
    int y= 0;
    for (String row : rows) {
      for (int x=0;x<row.length();x++) {
        if (row.charAt(x) == '@') {
          Player.pacman.pos = new Player.P(x,y);
        } else if (row.charAt(x) >= '1' && row.charAt(x) <='9') {
          int index = row.charAt(x)-'1';
          Player.ghosts[index].pos = new Player.P(x,y);
          board[x][y] = ' ';
        } else {
          board[x][y] = row.charAt(x);
        }
      }
      y++;
    }
    return board;
  }
}
