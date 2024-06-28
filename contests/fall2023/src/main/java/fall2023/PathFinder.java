package fall2023;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class PathFinder {

  int[] cameFrom = new int[18];
  int[] gScore = new int[18];
  int[] fScore = new int[18];
  
  
  static class Node {
    Pos pos;
    Node parent;
    Set<Pos> visited = new HashSet<>();
    int fScore, gScore;
  }
  
  
  Node best = null;
  int bestScore = Integer.MAX_VALUE;
  
  /**
   * return the optimal path that goes through all positions
   * @param startingPoint
   * @param goal
   * @param positions
   * @return
   */
  public List<Pos> think(Pos startingPoint, Pos goal, Set<Pos> checkpoints) {
    best = null;
    bestScore = Integer.MAX_VALUE;
    
    
    Node start = new Node();
    start.pos = startingPoint;
    start.parent = null;
    
    PriorityQueue<Node> openSet = new PriorityQueue<>((o1, o2) -> Integer.compare(o1.fScore, o2.fScore));
    openSet.add(start);
    
    while (!openSet.isEmpty()) {
      Node current = openSet.poll();
      
      if (current.visited.size() == checkpoints.size()) {
        // add the goal and go !
        current.gScore += current.pos.dist(goal);
        if (current.gScore < bestScore) {
          bestScore = current.gScore;
          best = current;
          System.out.println("New best score : "+bestScore);
        }
      } else {
        for (Pos p : checkpoints) {
          if (!current.visited.contains(p)) {
            Node node = new Node();
            node.parent = current;
            node.pos = p;
            node.gScore = current.gScore + current.pos.dist(p);
            if (node.gScore < bestScore) {
              node.fScore = node.gScore + p.dist(goal);
              node.visited.clear();
              node.visited.addAll(current.visited);
              node.visited.add(p);
              openSet.add(node);
            }
          }
        }
      }
    }

    // 
    List<Pos> positions = new ArrayList<>();
    Node current = best;
    while (current != null) {
      positions.add(0, current.pos);
      current = current.parent;
    }
    positions.add(goal);
    
    
    
    
    
    
    return positions;
  }
}
