package botg;

import fast.read.FastReader;

public class Player {
  State state = new State();
  SimpleAI ai = new SimpleAI();
  
  public static void main(String args[]) {
    FastReader in = new FastReader(System.in);

    new Player().play(in);
  }

  public void play(FastReader in) {
    state.readInit(in);

    // game loop
    while (true) {
      state.read(in);

      ai.think(state);
      ai.output();
    }    
  }
}
