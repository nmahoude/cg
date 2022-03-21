package samegame.ai;

import java.util.Scanner;

import org.junit.Test;

import samegame.State;

public class BeamSearchTest {

  @Test
  public void fewChoices() throws Exception {
    String input = ""
        + "4 4 4 0 4 1 2 3 2 2 1 3 0 2 0 \r\n"
        + "4 0 4 1 2 0 3 2 4 1 0 0 2 3 2 \r\n"
        + "0 2 1 4 0 4 2 3 3 0 3 2 1 2 0 \r\n"
        + "4 3 4 1 3 1 3 2 0 2 4 2 0 4 3 \r\n"
        + "2 0 2 0 4 0 4 0 3 1 3 1 2 4 2 \r\n"
        + "4 1 3 2 3 2 1 2 4 0 4 2 0 3 1 \r\n"
        + "0 2 1 4 0 4 2 1 3 1 3 4 1 2 0 \r\n"
        + "4 3 4 1 3 1 3 2 0 2 4 2 0 1 3 \r\n"
        + "2 0 2 0 4 0 4 0 3 1 3 1 2 4 2 \r\n"
        + "4 1 3 2 3 2 1 2 4 3 4 2 0 3 1 \r\n"
        + "0 2 1 4 0 4 2 1 3 0 3 0 1 2 0 \r\n"
        + "4 3 4 1 3 1 3 2 0 2 4 2 0 1 3 \r\n"
        + "2 0 2 0 4 0 4 0 3 1 3 1 2 4 2 \r\n"
        + "0 1 3 2 3 2 1 2 4 0 4 2 1 3 1 \r\n"
        + "2 0 2 0 2 0 2 0 2 1 0 1 2 0 2 ";
    
    State state = new State();
    state.read(new Scanner(input));
    
    
    BeamSearch bs = new BeamSearch();

    bs.think(state);
    bs.think(state);

    
    for (int i =0;i<1000;i++) {
      bs.think(state);
    }
    
    
    for (int i=0;i<BeamSearch.MAX_LAYERS;i++) {
      System.err.println("nodes @ "+i+" "+bs.layers[i].statesFE);
      for (int j=0;j<bs.layers[i].statesFE;j++) {
        System.err.println("   Score "+bs.layers[i].states[j].score);
      }
    }
    
  }
}
