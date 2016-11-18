package stc3;

import java.util.Scanner;

import stc3.ai.AI;
import stc3.game.GameState;

class NewPlayer {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    GameState state = new GameState(in);
    
    AI ai = new AI(state, 0);
    while(true) {
      state.readState();
      ai.think();
      System.out.println(ai.output());
    }
    
  }
}
