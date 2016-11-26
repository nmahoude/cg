package fantasticBits.fb;

import java.util.Scanner;

import fantasticBits.fb.ai.AI;
import fantasticBits.fb.state.GameState;

public class Player {
  static GameState gameState = new GameState();
  static AI ai = new AI();
  
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    gameState.readInitValues(scanner);
    
    while (true) {
      gameState.readRoundValue(scanner);
      ai.setGameState(gameState);
      ai.think();
      
      System.out.println(ai.getNextMove());
    }
  }
}
