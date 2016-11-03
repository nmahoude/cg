package hypersonic.montecarlo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import hypersonic.Move;
import hypersonic.Simulation;

public class MonteCarlo {

  private static final int SIMULATION_COUNT = 1_000;
  public static final int MAX_DEPTH = 18 ;
  public Node root = new Node();
  
  public void init() {
    root = new Node();
  }
  
  public void simulate(Simulation simulation) {
    root.clear();
    root.simulation = new Simulation();
    root.simulation.copyFrom(simulation);
    
    for (int i=SIMULATION_COUNT;i>=0;i--) {
      root.simulate(0);
    }
  }
  
  public Move findNextBestMove() {
    int bestScore = Integer.MIN_VALUE;
    Move bestMove = null;
    for (Entry<Move, Node> entry : root.childs.entrySet()) {
      int score = entry.getValue().getBestScore();
      System.err.println("-----");
      System.err.println("["+entry.getKey()+"] ("+entry.getValue().count+" sim) with score of "+entry.getValue().getScore()+", total: "+score);
      debugBestMove(entry.getValue());
      if (score > bestScore) {
        bestScore = score;
        bestMove = entry.getKey();
      }
    }
    System.err.println("Choosen move : "+bestMove);
    return bestMove;
  }

  // try to explain why certain choices
  private void debugBestMove(Node n) {
    List<Node> nodes = new ArrayList<>();
    nodes.add(n);
    n.getNodeList(nodes);
    System.err.print("Path : ");
    int count=0;
    for (Node node : nodes) {
      System.err.print("->"+node.move+"("+node.simulation.board.me.position+","+node.getScore()+")");
      count++;
      if (count >= 8) {
        count = 0;
        System.err.println("");
        break;
      }
    }
    System.err.println("");
  }
}
