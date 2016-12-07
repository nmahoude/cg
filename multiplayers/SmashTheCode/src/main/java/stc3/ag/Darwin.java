package stc3.ag;

import stc3.ai.AI;
import stc3.ai.Move;
import stc3.game.GameState;

public class Darwin {

  private GameState gameState;
  private Move move1;
  private Move move2;

  
  public static void main(String[] args) {
    new Darwin().oneMatch();
  }
  public void oneMatch() {
    gameState = new GameState(null);
    gameState.generateRandomPairs();
    
    AI ai1 = new AI(gameState, 0);
    AI ai2 = new AI(gameState, 1);
    
    while(true) {
      gameState.prepare();
      ai1.think();
      ai2.think();
      
      move1 = ai1.outputMove();
      move2 = ai2.outputMove();
      System.out.println("Pair : "+gameState.pairs[0].toString());
      System.out.println("P1 : "+move1);
      System.out.println("P2 : "+move2);
      
      if (!gameState.playerInfos[0].applyMove(move1)) { move1 = null; }
      if (!gameState.playerInfos[1].applyMove(move2)) { move2 = null; }

      if (move1 == null || move2 == null) {
        break;
      } else {
        gameState.playerInfos[0].applyNuisance();
        gameState.playerInfos[1].applyNuisance();
        gameState.generateANewPair();
      }
      debugBoards();
    }
    debugWinner();
  }

  private void debugWinner() {
    if (player1Dead() && player2Dead()) {
      System.out.println("Tie");
    } else if (player1Dead()) {
      System.out.println("P2 won");
    } else if (player2Dead()) {
      System.out.println("P1 win");
    }
  }
  private boolean player2Dead() {
    return move2 == null;
  }
  private boolean player1Dead() {
    return move1 == null;
  }
  private void debugBoards() {
    String p1[] = gameState.playerInfos[0].board.getDebugString().split("\n");
    String p2[] = gameState.playerInfos[1].board.getDebugString().split("\n");
    
    for (int i=0;i<8;i++) {
      System.out.print("["+gameState.pairs[i].color1+","+gameState.pairs[i].color2+"] ");
    }
    System.out.println("");
    
    System.out.println("Points "+gameState.playerInfos[0].points+" / "+gameState.playerInfos[1].points);
    System.out.println("Skulls "+gameState.playerInfos[0].nuisance+" / "+gameState.playerInfos[1].nuisance);
    for (int i=0;i<12;i++) {
      System.out.print(p1[i]+"  "+p2[i]);
      System.out.println("");
    }
    System.out.println("");
  }
}
