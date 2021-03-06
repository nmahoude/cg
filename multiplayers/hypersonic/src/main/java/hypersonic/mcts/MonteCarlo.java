package hypersonic.montecarlo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import hypersonic.Move;
import hypersonic.Simulation;

public class MonteCarlo {

  private static final int SIMULATION_COUNT = 2_200;
  public static final int MAX_DEPTH = 10 ;
  public Node root = new Node();
  
  public void init() {
    root = new Node();
  }
  
  public void simulate(long duration, long startTime, final Simulation simulation) {
    root.clear();
    root.simulation = new Simulation();
    root.simulation.copyFrom(simulation);
    
    do {
      root.simulate(0);
    } while (System.nanoTime() - startTime < duration);
  }

  public Move simulateBeam(final Simulation simulation) {
    final Simulation sim = new Simulation();
    
    double bestScore = Integer.MAX_VALUE;
    Move bestMove = null;
    
    final List<Move> chain = new ArrayList<>(MAX_DEPTH);
    for (int i=SIMULATION_COUNT;i>=0;i--) {
      sim.copyFrom(simulation);
      chain.clear();
      for (int depth=0;depth<MAX_DEPTH;depth++) {
        final List<Move> moves = simulation.getPossibleMoves();
        final int choice = ThreadLocalRandom.current().nextInt(moves.size());
        final Move nextMove = moves.get(choice);
        chain.add(nextMove);
        sim.simulate(nextMove);
      }
      final double score = sim.getScoreHeuristic();
      if (score > bestScore ) {
        bestScore = score;
        bestMove = chain.get(0); 
      }
    }
    return bestMove;
  }

  public Move findNextBestMove() {
    double bestScore = Integer.MIN_VALUE;
    Move bestMove = null;
    for (final Entry<Move, Node> entry : root.childs.entrySet()) {
      final double score = entry.getValue().getBestScore();
      
      System.err.println("-----");
      System.err.println("["+entry.getKey()+"] ("+entry.getValue().count+" sim) with score of "+entry.getValue().getScore()+", total: "+score);
      debugBestMove(entry.getValue());
      
      if (score > bestScore) {
        bestScore = score;
        bestMove = entry.getKey();
      }
    }
    //System.err.println("Choosen move : "+bestMove);
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
