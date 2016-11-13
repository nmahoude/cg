package stc2;

import java.util.concurrent.ThreadLocalRandom;

public class MCTS {
  private static final int ONE_LINE_OF_SKULLS = 420;
  private static final double WORST_SCORE = -1_000_000;
  static int MAX_PLY = 50_000;
  public Game game;
  public BitBoard myBoard;
  public BitBoard otherBoard;
  
  
  ThreadLocalRandom random = ThreadLocalRandom.current();
  public int previousTotalSim = 0;
  public MCNode bestNode;
  public double bestScore;
  public int bestKey;
  public MCNode root;
  
  private int myMinCol;
  private int myTotalCol;
  private int oppMinCol;
  private int oppTotalCol;
  private String message;
  private double oppBestScore1;
  private double oppBestScore2;

  private int bestPointsAtDepth[] = new int[8];
  
  public MCTS() {
    root = MCNode.get();
  }
  
  public void simulate(boolean keepLastMoveSimualtion) {
    message = "";
    myMinCol = myBoard.getMinimalColumnHeight();
    myTotalCol = myBoard.getTotalColumnHeight();
    
    oppMinCol = otherBoard.getMinimalColumnHeight();
    oppTotalCol = otherBoard.getTotalColumnHeight();

    MCNode newRoot = null;
    if (keepLastMoveSimualtion && bestNode != null) {
      newRoot = bestNode;
      root.childs[bestKey] = null;
      message = "s:"+previousTotalSim+"/c:"+bestNode.simCount;
      //System.err.println("Rebuilding on previous bestNode with board :");
      //System.err.println(bestNode.board.getDebugString());
    } else {
      //System.err.println("Resetting moves cache");
      message = "s:"+previousTotalSim+"/RazC";
    }

    simulateOpponent();
    simulateMe(newRoot);
  }

  private void simulateMe(MCNode newRoot) {
    root.release();
    if (newRoot != null) {
      root = newRoot;
      //System.err.println("Root has already "+root.simCount+" sims");
      //showSubNodeSims();
    } else {
      root = MCNode.get();
      root.board.copyFrom(myBoard);
    }
    root.color1 = game.nextBalls[0];
    root.color2 = game.nextBalls2[0];
    
    int maxDepth  = getOptimizedDepth();
    //System.err.println("MaxDepth is "+maxDepth);
    
    clearBestPointsAtDepth();
    for (int ply=MAX_PLY;--ply>=0;) {
      root.simulate(game, 0, maxDepth, bestPointsAtDepth);
    }

    bestScore = WORST_SCORE;
    bestNode = null;
    bestKey = -1;
    
    previousTotalSim = 0;
    for (int key=0;key<24;key++) {
      int rot = keyToRotation(key);
      int column = keyToColumn(key);
      MCNode child = root.childs[key];
      if (child == null) {
        //System.err.println(""+key+" ("+column+","+rot+") -> null" );
        continue;
      }
      previousTotalSim+= child.simCount;
      double score = child.getScore();
      double bScore = child.getBestScore();
      //System.err.println(""+key+" ("+column+","+rot+") (sim="+child.simCount+") -> " + score + " --> "+bScore);
      if (score > Math.min(11-oppMinCol, 4)*ONE_LINE_OF_SKULLS) {
        message = "Killer move";
        bestScore = score;
        bestNode = child;
        bestKey = key;
        break;
      }
      if (bScore > bestScore) {
        bestScore = bScore;
        bestNode = child;
        bestKey = key;
      }
    }
//    System.err.println("Sim count = "+simCount);
//    showMyBestPointsPerDepth(); 
  }

  private void showMyBestPointsPerDepth() {
    System.err.println("BestPoints for me :");
    System.err.println("1 ply  : "+bestPointsAtDepth[0]+""); 
    System.err.println("2 plys : "+bestPointsAtDepth[1]+""); 
    System.err.println("3 plys : "+bestPointsAtDepth[2]+""); 
    System.err.println("4 plys : "+bestPointsAtDepth[3]+""); 
    System.err.println("5 plys : "+bestPointsAtDepth[4]+""); 
    System.err.println("6 plys : "+bestPointsAtDepth[5]+""); 
    System.err.println("7 plys : "+bestPointsAtDepth[6]+""); 
    System.err.println("8 plys : "+bestPointsAtDepth[7]+"");
  }

  private void clearBestPointsAtDepth() {
    bestPointsAtDepth[0] = 0;
    bestPointsAtDepth[1] = 0;
    bestPointsAtDepth[2] = 0;
    bestPointsAtDepth[3] = 0;
    bestPointsAtDepth[4] = 0;
    bestPointsAtDepth[5] = 0;
    bestPointsAtDepth[6] = 0;
    bestPointsAtDepth[7] = 0;
  }

  private int getOptimizedDepth() {
    if (oppBestScore1 >= ONE_LINE_OF_SKULLS*6) {
      return 1;
    } else if (oppBestScore1 >= ONE_LINE_OF_SKULLS * 4) {
      return 2;
    } else if (oppBestScore2 >= ONE_LINE_OF_SKULLS*6) {
      return 2;
    }
    return 7;
  }

  private void simulateOpponent() {

    root.release();
    root = MCNode.get();
    root.board.copyFrom(otherBoard);
    
    root.color1 = game.nextBalls[0];
    root.color2 = game.nextBalls2[0];
    
    clearBestPointsAtDepth();
    for (int i=0;i<400;i++) {
      root.simulate(game, 0, 2, bestPointsAtDepth);
    }
    
    oppBestScore1 = WORST_SCORE;
    MCNode oppBestNode1 = null;
    Integer oppBestKey1 = null;
    oppBestScore2 = WORST_SCORE;
    MCNode oppBestNode2 = null;
    Integer oppBestKey2 = null;
    
    for (int key=0;key<24;key++) {
      MCNode child = root.childs[key];
      if (child == null) continue;

      double score1 = child.getScore();
      double score2 = child.getBestScore();
      int rot = keyToRotation(key);
      int column = keyToColumn(key);
      
      //System.err.println(""+key+" ("+column+","+rot+") (sim="+child.count+") -> " + score1 + " --> "+score2);
      if (score1 > oppBestScore1) {
        oppBestScore1 = score1;
        oppBestNode1 = child;
        oppBestKey1 = key;
      }
      if (score2 > oppBestScore2) {
        oppBestScore2 = score2;
        oppBestNode2 = child;
        oppBestKey2 = key;
      }
    }
    
    //System.err.println("Opponents opportunity: "+oppBestScore1+" / "+oppBestScore2);
//    System.err.println("Best points for him/her:");
//    System.err.println("1 ply : "+bestPointsAtDepth[0]+""); 
//    System.err.println("2 plys : "+bestPointsAtDepth[1]+""); 
  }

  
  public String output() {
    int key = bestKey;
    int rot = keyToRotation(key);
    int column = keyToColumn(key);
    return ""+column+" "+rot+ " "+message;
  }

  private int keyToColumn(int key) {
    return key >>> 2;
  }

  private int keyToRotation(int key) {
    return key & 0b11;
  }

  public void attachGame(Game game) {
    this.game = game;
    myBoard  =game.myBoard;
    otherBoard = game.otherBoard;
  }

  public int getSkullCountAfterMove() {
    return bestNode.board.layers[BitBoard.SKULL_LAYER].bitCount();
  }
}
