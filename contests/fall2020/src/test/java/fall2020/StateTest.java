package fall2020;

import org.junit.jupiter.api.Test;

import fall2020.ai.heuristic.SimpleAI;
import fall2020.fast.FastReader;

public class StateTest {

  
  private static final String EOF = "\n\r";

  @Test
  public void simpleRead() throws Exception {
    String input = ""+
        "31"+EOF+
        " 62 BREW 0 -2 0 -3 19 3 2 0 0"+EOF+
        " 59 BREW -2 0 0 -3 15 1 4 0 0"+EOF+
        " 50 BREW -2 0 0 -2 10 0 0 0 0"+EOF+
        " 64 BREW 0 0 -2 -3 18 0 0 0 0"+EOF+
        " 54 BREW 0 -2 0 -2 12 0 0 0 0"+EOF+
        " 11 LEARN -4  0  2  0  0  0  1  0  1"+EOF+
        " 20 LEARN  2 -2  0  1  0  1  0  0  1"+EOF+
        "  6 LEARN  2  1 -2  1  0  2  0  0  1"+EOF+
        "  9 LEARN  2 -3  2  0  0  3  0  0  1"+EOF+
        " 34 LEARN -2  0 -1  2  0  4  0  0  1"+EOF+
        " 31 LEARN  0  3  2 -2  0  5  0  0  1"+EOF+
        " 78 CAST  2  0  0  0  0 -1 -1  1  0"+EOF+
        " 79 CAST -1  1  0  0  0 -1 -1  1  0"+EOF+
        " 80 CAST  0 -1  1  0  0 -1 -1  0  0"+EOF+
        " 81 CAST  0  0 -1  1  0 -1 -1  0  0"+EOF+
        " 86 CAST  3  0  0  0  0 -1 -1  1  0"+EOF+
        " 88 CAST -2  2  0  0  0 -1 -1  0  1"+EOF+
        " 90 CAST  0  2 -2  1  0 -1 -1  1  1"+EOF+
        " 94 CAST -3  1  1  0  0 -1 -1  1  1"+EOF+
        " 95 CAST  4  0  0  0  0 -1 -1  1  0"+EOF+
        " 96 CAST  0  0 -2  2  0 -1 -1  1  1"+EOF+
        " 82 OPPONENT_CAST  2  0  0  0  0 -1 -1  0  0"+EOF+
        " 83 OPPONENT_CAST -1  1  0  0  0 -1 -1  0  0"+EOF+
        " 84 OPPONENT_CAST  0 -1  1  0  0 -1 -1  0  0"+EOF+
        " 85 OPPONENT_CAST  0  0 -1  1  0 -1 -1  0  0"+EOF+
        " 87 OPPONENT_CAST  3  0  0  0  0 -1 -1  1  0"+EOF+
        " 89 OPPONENT_CAST -2  2  0  0  0 -1 -1  0  1"+EOF+
        " 91 OPPONENT_CAST  0  2 -2  1  0 -1 -1  1  1"+EOF+
        " 92 OPPONENT_CAST  3 -1  0  0  0 -1 -1  1  1"+EOF+
        " 93 OPPONENT_CAST  0  0  1  0  0 -1 -1  0  0"+EOF+
        " 97 OPPONENT_CAST -5  0  0  2  0 -1 -1  1  1"+EOF+
        "2 0 0 0 15"+EOF+
        "2 0 0 0 15"+EOF+
        "";
    
    State state = new State();
    
    state.read(new FastReader(input.getBytes()));
    SimpleAI ai = new SimpleAI();
    String result = ai.think(state);
    
    System.out.println(result);
    
  }
  
}
