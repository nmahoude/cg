package cultistwars;

import fast.read.FastReader;

/**
 * Convert neutral units and attack enemy ones
 **/
public class Player {

  private State state = new State();
  private AI ai = new AI();
  
  public static void main(String args[]) {
    FastReader in = new FastReader(System.in);

    new Player().play(in);
  }

  private void play(FastReader in) {
    state.readInit(in);

    // game loop
    while (true) {
      state.readTurn(in);

      ai.think(state);
      
    }
  }
}