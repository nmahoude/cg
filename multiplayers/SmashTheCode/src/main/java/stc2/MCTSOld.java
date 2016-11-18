package stc2;

import java.util.concurrent.ThreadLocalRandom;

public class MCTSOld {

  public static class AjustementVariables {
    public int THRESOLD_DEPTH_1_COLUMN = 6;
    public int THRESOLD_DEPTH_2_COLUMN = 2;
    public int MIN_SKULLS_COLUMNS_TO_DROP = 4;
    public int SCORE_TO_DESTROY_SKULLS_RAPIDLY = 40;
  }
  public AjustementVariables ajust = new AjustementVariables();
  
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
  
  public MCTSOld() {
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
    int plies = 0;
    long nanoTime = 0;
    do {
      for (int ply=1_000;--ply>=0;) {
        plies++;
        root.simulate(game, 0, maxDepth, bestPointsAtDepth);
      }
      nanoTime = System.nanoTime();
    } while (nanoTime - game.nanoStart < 65_000_000);

    message += " / "+plies+" in "+((nanoTime - game.nanoStart) / 1_000_000);
    bestScore = WORST_SCORE;
    bestNode = null;
    bestKey = -1;
    
    previousTotalSim = 0;
    int maxP1 = Integer.MIN_VALUE;
    int maxP2 = Integer.MIN_VALUE;
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
      int sp1 = child.getBestPoints(1);
      int sp2 = child.getBestPoints(2);
      
      if (sp1 > maxP1) { maxP1 = sp1; }
      if (sp2 > maxP2) { maxP2 = sp2; }
      //System.err.println(""+key+" ("+column+","+rot+") (sim="+child.simCount+") -> " + score + " --> "+bScore);
      if (sp1 >= ajust.SCORE_TO_DESTROY_SKULLS_RAPIDLY && myTotalCol > 60) {
        message += "kills skulls";
        bestScore = score;
        bestNode = child;
        bestKey = key;
        break;
      }
      
      if (child.simulation.points > Math.min(11-oppMinCol, ajust.MIN_SKULLS_COLUMNS_TO_DROP)*ONE_LINE_OF_SKULLS) {
        int rows = child.simulation.points  / ONE_LINE_OF_SKULLS;
        message += "KM att(" + rows+")";
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
    message+="me("+maxP1+"->"+maxP2+")";
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
    if (oppBestScore1 >= ONE_LINE_OF_SKULLS*ajust.THRESOLD_DEPTH_1_COLUMN) {
      return 1;
    } else if (oppBestScore2 >= ONE_LINE_OF_SKULLS*ajust.THRESOLD_DEPTH_2_COLUMN) {
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
    message =" opp("+oppBestScore1+"->"+oppBestScore2+")";
    //System.err.println("Opponents opportunity: "+oppBestScore1+" / "+oppBestScore2);
//    System.err.println("Best points for him/her:");
//    System.err.println("1 ply : "+bestPointsAtDepth[0]+""); 
//    System.err.println("2 plys : "+bestPointsAtDepth[1]+""); 
  }

  
  public String output() {
    if (bestNode == null) {
      return "0 0 DEAD";
    }
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

  public void attachGame(Game game, BitBoard board, BitBoard otherBoard) {
    this.game = game;
    this.myBoard  = board;
    this.otherBoard = otherBoard;
  }

  public int getSkullCountAfterMove() {
    if (bestNode == null) {
      return 72;
    } 
    return bestNode.board.layers[BitBoard.SKULL_LAYER].bitCount();
  }
}
