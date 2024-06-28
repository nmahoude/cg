package fall2020.ai.mc;

import org.junit.jupiter.api.Test;

import fall2020.State;
import fall2020.fast.FastReader;

public class SimpleMCTest {
  private static final String EOF = "\n\r";
  
  @Test
  void IntegrationTest() throws Exception {
    String input = ""+
    "31"+EOF+
    " 51 BREW -2 0 -3 0 14 3 4 0 0"+EOF+
    " 52 BREW -3 0 0 -2 12 1 3 0 0"+EOF+
    " 46 BREW -2 -3 0 0 8 0 0 0 0"+EOF+
    " 54 BREW 0 -2 0 -2 12 0 0 0 0"+EOF+
    " 43 BREW -3 -2 0 0 7 0 0 0 0"+EOF+
    " 24 LEARN  0  3  0 -1  0  0  7  0  1"+EOF+
    "  7 LEARN  3  0  1 -1  0  1  3  0  1"+EOF+
    "  5 LEARN  2  3 -2  0  0  2  1  0  1"+EOF+
    "  4 LEARN  3  0  0  0  0  3  1  0  0"+EOF+
    " 41 LEARN  0  0  2 -1  0  4  0  0  1"+EOF+
    " 39 LEARN  0  0 -2  2  0  5  0  0  1"+EOF+
    " 78 CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
    " 79 CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
    " 80 CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
    " 81 CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
    " 86 CAST  1 -3  1  1  0 -1 -1  1  1"+EOF+
    " 87 CAST -4  0  2  0  0 -1 -1  1  1"+EOF+
    " 88 CAST  3 -1  0  0  0 -1 -1  1  1"+EOF+
    " 89 CAST  4  0  0  0  0 -1 -1  1  0"+EOF+
    " 90 CAST -4  0  1  1  0 -1 -1  1  1"+EOF+
    " 91 CAST  0  3  2 -2  0 -1 -1  1  1"+EOF+
    " 92 CAST  0 -3  3  0  0 -1 -1  1  1"+EOF+
    " 93 CAST -3  3  0  0  0 -1 -1  1  1"+EOF+
    " 95 CAST  1  2 -1  0  0 -1 -1  0  1"+EOF+
    " 96 CAST -5  0  3  0  0 -1 -1  1  1"+EOF+
    " 97 CAST  1  1  1 -1  0 -1 -1  1  1"+EOF+
    " 82 OPPONENT_CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
    " 83 OPPONENT_CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
    " 84 OPPONENT_CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
    " 85 OPPONENT_CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
    " 94 OPPONENT_CAST  1  0  1  0  0 -1 -1  1  0"+EOF+
    "7 2 0 0 0"+EOF+
    "1 0 1 0 15"+EOF+
    "";
    
    
    State state = new State();
    state.read(new FastReader(input.getBytes()));
    
    SimpleMC mc = new SimpleMC();
    String output = mc.think(state);

    
    for (int i=0;i<10_000;i++) {
      output = mc.think(state);
    }
    
    
    System.err.println("MC output "+output);
  }
  
  //PERF 
  public static void main(String[] args) throws Exception  {
    String input = ""+
        "31"+EOF+
        " 51 BREW -2 0 -3 0 14 3 4 0 0"+EOF+
        " 52 BREW -3 0 0 -2 12 1 3 0 0"+EOF+
        " 46 BREW -2 -3 0 0 8 0 0 0 0"+EOF+
        " 54 BREW 0 -2 0 -2 12 0 0 0 0"+EOF+
        " 43 BREW -3 -2 0 0 7 0 0 0 0"+EOF+
        " 24 LEARN  0  3  0 -1  0  0  7  0  1"+EOF+
        "  7 LEARN  3  0  1 -1  0  1  3  0  1"+EOF+
        "  5 LEARN  2  3 -2  0  0  2  1  0  1"+EOF+
        "  4 LEARN  3  0  0  0  0  3  1  0  0"+EOF+
        " 41 LEARN  0  0  2 -1  0  4  0  0  1"+EOF+
        " 39 LEARN  0  0 -2  2  0  5  0  0  1"+EOF+
        " 78 CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
        " 79 CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
        " 80 CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
        " 81 CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
        " 86 CAST  1 -3  1  1  0 -1 -1  1  1"+EOF+
        " 87 CAST -4  0  2  0  0 -1 -1  1  1"+EOF+
        " 88 CAST  3 -1  0  0  0 -1 -1  1  1"+EOF+
        " 89 CAST  4  0  0  0  0 -1 -1  1  0"+EOF+
        " 90 CAST -4  0  1  1  0 -1 -1  1  1"+EOF+
        " 91 CAST  0  3  2 -2  0 -1 -1  1  1"+EOF+
        " 92 CAST  0 -3  3  0  0 -1 -1  1  1"+EOF+
        " 93 CAST -3  3  0  0  0 -1 -1  1  1"+EOF+
        " 95 CAST  1  2 -1  0  0 -1 -1  0  1"+EOF+
        " 96 CAST -5  0  3  0  0 -1 -1  1  1"+EOF+
        " 97 CAST  1  1  1 -1  0 -1 -1  1  1"+EOF+
        " 82 OPPONENT_CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
        " 83 OPPONENT_CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
        " 84 OPPONENT_CAST  0 -1  1  0  0 -1 -1  1  0"+EOF+
        " 85 OPPONENT_CAST  0  0 -1  1  0 -1 -1  1  0"+EOF+
        " 94 OPPONENT_CAST  1  0  1  0  0 -1 -1  1  0"+EOF+
        "7 2 0 0 0"+EOF+
        "1 0 1 0 15"+EOF+
        "";
        
        
        State state = new State();
        state.read(new FastReader(input.getBytes()));
        
        SimpleMC mc = new SimpleMC();
        String output = mc.think(state);

        
        for (int i=0;i<1_000;i++) {
          output = mc.think(state);
        }
        
        
        System.err.println("MC output "+output);
  }
}
