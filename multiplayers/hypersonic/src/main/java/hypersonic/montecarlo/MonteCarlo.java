package hypersonic.montecarlo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import hypersonic.Move;
import hypersonic.Simulation;

public class MonteCarlo {

  private static final int SIMULATION_COUNT = 1_200;
  public static final int MAX_DEPTH = 18 ;
  public Node root = new Node();
  
  public void init() {
    root = new Node();
  }
  
  public void simulate(final Simulation simulation) {
    root.clear();
    root.simulation = new Simulation();
    root.simulation.copyFrom(simulation);
    
    for (int i=SIMULATION_COUNT;i>=0;i--) {
      root.simulate(0);
    }
  }

  public Move findNextBestMove() {
    double bestScore = Integer.MIN_VALUE;
    Move bestMove = null;
    for (final Entry<Move, Node> entry : root.childs.entrySet()) {
      final double score = entry.getValue().getBestScore();
      //System.err.println("-----");
      //System.err.println("["+entry.getKey()+"] ("+entry.getValue().count+" sim) with score of "+entry.getValue().getScore()+", total: "+score);
      //debugBestMove(entry.getValue());
      if (score > bestScore) {
        bestScore = score;
        bestMove = entry.getKey();
      }
    }
    System.err.println("Choosen move : "+bestMove);
    return bestMove;
  }

  // try to explain why certain choices
  private void debugBestMove(final Node n) {
    final List<Node> nodes = new ArrayList<>();
    nodes.add(n);
    n.getNodeList(nodes);
    System.err.print("Path : ");
    int count=0;
    for (final Node node : nodes) {
      System.err.print(""+node.move+"("/*+node.simulation.board.me.position+","*/+node.getScore()+")");
      count++;
      if (count >= 20) {
        count = 0;
        System.err.println("");
        break;
      }
    }
    System.err.println("");
  }
  
  void debugAllMoves(final Node n) {
    final List<Node> nodes = new ArrayList<>();
    nodes.add(n);
    n.getNodeList(nodes);
    System.out.print("Path : ");
    int count=0;
    for (final Node node : nodes) {
      System.out.print(""+node.move+"("/*+node.simulation.board.me.position+","*/+node.getScore()+")");
      count++;
      if (count >= 20) {
        count = 0;
        System.out.println("");
        break;
      }
    }
    System.out.println("");
  }
  
}
