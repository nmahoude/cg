package samegame;

import java.util.Scanner;

import samegame.ai.BeamSearch;

public class Player {
  public static int turn = 0;
  State state = new State();
  BeamSearch bs = new BeamSearch();
  
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);

    new Player().play(in);
  }

  private void play(Scanner in) {
    while (true) {
      state.read(in);
      turn++;
      System.err.println("turn = "+turn);
      state.print();
      
      Pos bestChild = bs.think(state);
      
      System.out.println(""+bestChild.x+" "+bestChild.y+" Hello BeamSearch\\n:-)"); // Selected tile "x y [message]".
    }

  }


}
