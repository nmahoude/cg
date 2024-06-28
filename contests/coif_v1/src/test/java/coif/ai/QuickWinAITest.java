package coif.ai;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Scanner;

import org.junit.Test;

import coif_old.Pos;
import coif_old.State;
import coif_old.ai.QuickWinAI;

public class QuickWinAITest {

  @Test
  public void cost3ToPassThroughTower() {
    String input = "138 51\r\n" + 
        "132 51\r\n" + 
        "OOO#OOOOOO.#\r\n" + 
        "OOOOOOOOOOO#\r\n" + 
        ".OOOOOOOOO##\r\n" + 
        "#OOOOOOOOX##\r\n" + 
        "OOOOOOOOXXX#\r\n" + 
        "OOOOOOOXXXXX\r\n" + 
        "OOOOOOXXXXXX\r\n" + 
        "#OOOOXXXXXXX\r\n" + 
        "##OOxOXXXXX#\r\n" + 
        "##OOOOOXXXXX\r\n" + 
        "#oXXXOXXXXXX\r\n" + 
        "#XXXXXXX#XXX\r\n" + 
        "6\r\n" + 
        "0 0 0 0\r\n" + 
        "1 0 11 11\r\n" + 
        "1 1 9 11\r\n" + 
        "1 1 10 5\r\n" + 
        "1 1 10 11\r\n" + 
        "1 2 10 10\r\n" + 
        "32\r\n" + 
        "0 1 1 4 5\r\n" + 
        "0 2 1 6 4\r\n" + 
        "0 6 1 7 2\r\n" + 
        "0 8 1 4 9\r\n" + 
        "0 9 1 8 3\r\n" + 
        "0 11 1 5 6\r\n" + 
        "0 13 1 4 7\r\n" + 
        "0 17 1 6 5\r\n" + 
        "0 18 1 6 2\r\n" + 
        "0 19 1 7 1\r\n" + 
        "0 21 1 3 9\r\n" + 
        "0 22 1 8 2\r\n" + 
        "0 24 1 7 4\r\n" + 
        "0 25 1 9 0\r\n" + 
        "0 27 1 5 10\r\n" + 
        "0 28 1 9 1\r\n" + 
        "0 30 1 6 9\r\n" + 
        "0 31 1 9 2\r\n" + 
        "0 32 1 10 1\r\n" + 
        "0 33 1 5 8\r\n" + 
        "1 3 1 6 6\r\n" + 
        "1 4 1 5 7\r\n" + 
        "1 5 1 7 6\r\n" + 
        "1 7 1 7 5\r\n" + 
        "1 10 1 8 4\r\n" + 
        "1 15 1 6 7\r\n" + 
        "1 16 1 4 10\r\n" + 
        "1 20 1 9 4\r\n" + 
        "1 23 1 2 10\r\n" + 
        "1 26 1 3 10\r\n" + 
        "1 29 1 9 3\r\n" + 
        "1 34 1 1 11";
    
    State state = new State();
    state.readTurn(new Scanner(input));
    
    QuickWinAI ai = new QuickWinAI(state);
    
    //ai.think();
    int lvl = ai.getLevelToConquerCell(Pos.get(10, 11));
    
    
    assertThat(lvl).isEqualTo(3);
  }
  
  @Test
  public void canQuickWin() {
    String input = "174 42\r\n" + 
        "23 9\r\n" + 
        "OOO#########\r\n" + 
        "OOO###OO####\r\n" + 
        "OOO##OOOO###\r\n" + 
        "#OOOOOOOOO##\r\n" + 
        "#OOO###xOOX#\r\n" + 
        "#OOOO##oXOX#\r\n" + 
        "#OOOO##oXXX#\r\n" + 
        "#OOOO###X.X#\r\n" + 
        "##OOOOOOO.X#\r\n" + 
        "###OOOO##.XX\r\n" + 
        "####OO###.XX\r\n" + 
        "#########.XX\r\n" + 
        "2\r\n" + 
        "0 0 0 0\r\n" + 
        "1 0 11 11\r\n" + 
        "16\r\n" + 
        "0 1 1 7 3\r\n" + 
        "0 2 1 4 7\r\n" + 
        "0 5 1 8 4\r\n" + 
        "0 7 1 5 8\r\n" + 
        "0 9 1 4 8\r\n" + 
        "0 12 1 3 8\r\n" + 
        "0 13 1 9 4\r\n" + 
        "0 14 1 5 9\r\n" + 
        "0 15 1 8 2\r\n" + 
        "0 16 1 5 10\r\n" + 
        "0 18 1 6 8\r\n" + 
        "0 21 1 8 8\r\n" + 
        "0 22 1 9 5\r\n" + 
        "1 3 1 10 4\r\n" + 
        "1 4 1 11 9\r\n" + 
        "1 20 2 8 7";
    
    State state = new State();
    state.readTurn(new Scanner(input));
    
    QuickWinAI ai = new QuickWinAI(state);
    
    ai.think();
    assertThat(ai.wannaPlay).isEqualTo(true);
  }
  
  @Test
  public void dontCrash() {
    String input = "100 31\r\n" + 
        "9 2\r\n" + 
        "OOO#OOO#####\r\n" + 
        "OOOOOOO#####\r\n" + 
        "OOO#OOOO####\r\n" + 
        "#OO###OO####\r\n" + 
        "#OOO#XXOO###\r\n" + 
        "#OOOOXXOOX##\r\n" + 
        "##OXXXOOOXX#\r\n" + 
        "###oXOO#XXX#\r\n" + 
        "####XX###XX#\r\n" + 
        "####XXXX#XX.\r\n" + 
        "#####XXXXXXX\r\n" + 
        "#####XXX#XXX\r\n" + 
        "8\r\n" + 
        "0 0 0 0\r\n" + 
        "0 2 1 1\r\n" + 
        "1 0 11 11\r\n" + 
        "1 1 8 10\r\n" + 
        "1 2 4 8\r\n" + 
        "1 2 5 9\r\n" + 
        "1 2 7 9\r\n" + 
        "1 2 7 10\r\n" + 
        "18\r\n" + 
        "0 6 1 7 2\r\n" + 
        "0 8 1 6 2\r\n" + 
        "0 13 1 7 4\r\n" + 
        "0 18 1 7 5\r\n" + 
        "0 21 1 2 4\r\n" + 
        "0 25 1 6 6\r\n" + 
        "0 26 1 8 5\r\n" + 
        "0 27 1 4 5\r\n" + 
        "0 31 1 5 7\r\n" + 
        "0 32 1 8 6\r\n" + 
        "1 5 1 7 11\r\n" + 
        "1 10 2 5 6\r\n" + 
        "1 11 2 5 11\r\n" + 
        "1 14 1 4 7\r\n" + 
        "1 15 2 4 9\r\n" + 
        "1 28 3 3 6\r\n" + 
        "1 29 2 5 4\r\n" + 
        "1 30 1 6 4";
    
    State state = new State();
    state.readTurn(new Scanner(input));
    
    QuickWinAI ai = new QuickWinAI(state);
    
    ai.think();
    assertThat(ai.wannaPlay).isEqualTo(true);
    ai.output();
  }
}
