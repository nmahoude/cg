package stc3;

import java.util.Scanner;

import stc3.ai.AI;
import stc3.game.GameState;

public class Player3 {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    GameState state = new GameState(in);
    
    AI ai = new AI(state, 0);
    while(true) {
      state.readState();
      state.prepare();
      ai.think();
      System.out.println(ai.output());
    }
    
  }
}
