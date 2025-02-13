package stc3.ai;

import stc3.game.GameState;
import stc3.mcts.MCTS;
import stc3.mcts.Node;

public class AI {
  private GameState gameState;
  private int playerIndex;
  private Move move = new Move(0,0,"Not Implemented");

  public AI(GameState state, int playerIndex) {
    this.gameState = state;
    this.playerIndex = playerIndex;
  }

  public void think() {
    if (playerIndex == 1) {
      int key = GameState.random.nextInt(24);
      if (key == 0+2*6) key +=1;
      if (key == 5+0*6) key+=1;
      move = new Move(key  % 6, key / 6, "pure Random");
    } else {
      MCTS mcts = new MCTS();
      mcts.maxDepth = 8;
      Node root = mcts.run(gameState.playerInfos[playerIndex], 10);
      Node bestChild = root.findBestChildForAbsoluteScore();
      if (bestChild == null) {
        move = new Move(0,0, "DEAD");
      } else {
        doSomeDebug(root, bestChild);
        move = new Move(bestChild.column, bestChild.rotation, "MCTS ("+root.simCount+")");
      }
    }
  }

  private void doSomeDebug(Node root, Node bestNode) {
    System.err.println("Pairs to come");
    for (int i=0;i<8;i++) {
      System.err.print(gameState.playerInfos[playerIndex].pairs[i]);
    }
    System.err.println("");
    System.err.println("Childs of root :");
    for (Node node :root.visited) {
      System.err.println("["+node.column+","+node.rotation+"] score("+node.score +","+ node.constructionScore+") sim("+node.simCount+")");
    }
  }

  public Move outputMove() {
    return move;
  }
  
  public String output() {
    return outputMove().toString();
  }
}
