package clashofbots;

import fast.read.FastReader;

public class Player {
  public static State state = new State();

  public static void main(String args[]) {
    FastReader in = new FastReader(System.in);

    // game loop
    while (true) {

      state.read(in);
      
      for (int i = 0; i < state.numberOfRobots; i++) {
        
        Robot current = state.robots.get(i);
        current.think(state);
        System.out.println(current.command);
      }
    }
  }
}
