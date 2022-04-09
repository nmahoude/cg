package othello;

import java.util.Scanner;

public class Player {
  public static boolean debugInput = false;

  Depth1AI ai = new Depth1AI();
  
  State state = new State();

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    new Player().play(in);
  }

  private void play(Scanner in) {
    state.readInit(in);

    // game loop
    while (true) {
      state.read(in);
      state.debug();
      
      
      String output = ai.think(state);
      
      System.out.println(output);
    }

  }
}
