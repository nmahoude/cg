package othello;

import java.util.Scanner;

public class Player {
  public static boolean debugInput = false;

  Depth1AI ai = new Depth1AI();
  
  State state = new State();
  static int turn;

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    new Player().play(in);
  }

  private void play(Scanner in) {
    state.readInit(in);
    
    // game loop
    while (true) {
      turn++;
      state.read(in);
      state.outputTestValues();
      state.debug();
      
      
      String output = ai.think(state);
      
      System.out.println(output);
    }

  }
}
