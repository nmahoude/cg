package tge.heuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import tge.Agent;
import tge.Cell;
import tge.Player;
import tge.Point;
import tge.WallOrientation;
import tge.paths.AStar;
import tge.simulation.Action;
import tge.simulation.ActionType;
import tge.simulation.Simulation;

public class Heuristic2 {
  Simulation simulation = new Simulation();
  
  Map<Agent, List<Action>> wallMakesLongerPath = new HashMap<>();
  
  List<Node> nodes = new ArrayList<>();
  List<Node> doneNodes = new ArrayList<>();
  
  public Action getBestAction() {
    long start = System.currentTimeMillis();
    Agent agent = Player.agents[Player.myId];
    
    getMoves(agent);
    if (agent.wallLefts > 0) {
      getPossibleWalls(agent);
    }

    double maxScore = Double.NEGATIVE_INFINITY;
    Node bestNode = null;
    Node bestMove = null;
    while (!nodes.isEmpty()) {
      Node node = nodes.remove(0);
      doneNodes.add(node);
      
      if (node.bonus > 0) {
        System.err.println("Using bonus nodes :" + node.action.toOutput());
      }
      if (!simulation.play(node.action)) {
        continue;
      }
      double score = evaluate(node);
      simulation.unplay(node.action);

      if (score > maxScore) {
        maxScore = score;
        bestNode = node;
        if (node.action.type == ActionType.MOVE) {
          bestMove = node;
        }
      }
    }

    long end = System.currentTimeMillis();
    System.err.println("Time of heuristic " + (end - start));
    if (Player.agents[Player.myId].clearPath && Player.agents[Player.myId].currentPath.size() <= Player.bandit.currentPath.size()) {
      // TODO maybe try t block 3rd player if any 
      System.err.println("We have the clear path and it's lower than enemy, so go get it");
      bestMove.action.output = "Best path";
      return bestMove.action;
    } else {
      return bestNode.action;
    }
  }

  public double evaluate(Node node) {
    int dist1 = -1;
    int dist2 = -1;
    int dist[] = getDistances();
    for (int i=0;i<Player.playerCount;i++) {
      if (dist[i] > Player.agents[i].currentPath.size() && node.action.type == ActionType.WALL) {
        // one wall exists that can make player way longer
        if (Player.agents[i].clearPath) System.err.println("*** "+i+" has no clear path ***");
        Player.agents[i].clearPath = false;
        Player.agents[i].maxPath = Math.max(Player.agents[i].maxPath, dist[i]);
      }
      if (dist[i] == -100) continue;
      if (dist[i] == -1) return Double.NEGATIVE_INFINITY;
      if (i == Player.myId) dist1 = dist[i];
      if (i == hisId()) dist2 = dist[i];
    }

    if (dist1 == 0)  { /* we won */ node.score = 1_000_000; return node.score; }
    if (dist2 == 1)  { /* he won */ node.score = -1_000_000; return node.score; }

    double score = -dist1 + dist2; 
    System.err.println(node.action.toOutput() +" = "+ score);
    if (node.action.type == ActionType.WALL) {
      if (Player.me.currentPath.size() < Player.bandit.currentPath.size()) score -= 10;
      score -= 0.01*distToWall(node.action, Player.bandit.position);
    }
    return score;
  }

  private int distToWall(Action action, Point position) {
    if (action.orientation == WallOrientation.HORIZONTAL) {
      double cell1 = position.manathan(action.position);
      double cell2 = position.manathan(Point.get(action.position.x, action.position.y-1));
      double cell3 = position.manathan(Point.get(action.position.x+1, action.position.y));
      double cell4 = position.manathan(Point.get(action.position.x+1, action.position.y-1));
      
      return (int)Math.min(Math.min(cell1, cell2), Math.min(cell3, cell4));
    } else {
      double cell1 = position.manathan(action.position);
      double cell2 = position.manathan(Point.get(action.position.x-1, action.position.y));
      double cell3 = position.manathan(Point.get(action.position.x, action.position.y+1));
      double cell4 = position.manathan(Point.get(action.position.x-1, action.position.y+1));
      
      return (int)Math.min(Math.min(cell1, cell2), Math.min(cell3, cell4));
    }
 }

  int[] getDistances() {
    int dist[] = new int[3];
    for (int i = 0; i < Player.playerCount; i++) {
      if (Player.agents[i].position == Point.unknown) {
        dist[i] = -100; // arrived already
        continue;
      }
      List<Cell> path1 = AStar.astar(Player.grid.get(Player.agents[i].position), i);
      dist[i] = path1.size() - 1;
    }
    return dist;
  }
  
  int hisId() {
    int i = (Player.myId + 1) % Player.playerCount;
    while (Player.agents[i].position == Point.unknown) {
      i = (i + 1) % Player.playerCount;
    }
    return i;
  }

  private void getPossibleWalls(Agent agent) {
    for (int y = 0; y < 9; y++) {
      for (int x = 0; x < 9; x++) {
        if (x != 0) {
          Action action = new Action();
          action.agent = agent;
          action.type = ActionType.WALL;
          action.orientation = WallOrientation.VERTICAL;
          action.position = Point.get(x, y);
          Node node = new Node();
          node.action = action;
          nodes.add(node);
        }
        if (y != 0) {
          Action action = new Action();
          action = new Action();
          action.agent = agent;
          action.type = ActionType.WALL;
          action.orientation = WallOrientation.HORIZONTAL;
          action.position = Point.get(x, y);
          Node node = new Node();
          node = new Node();
          node.action = action;
          nodes.add(node);
        }
      }
    }
  }

  private void getMoves(Agent agent) {
    Cell cell = Player.grid.get(agent.position);

    for (int i = 0; i < 4; i++) {
      if (cell.walls[i] != 0)
        continue;
      Action action = new Action();
      action.agent = agent;
      action.type = ActionType.MOVE;
      action.moveIndex = i;
      Node node = new Node();
      node.action = action;
      nodes.add(node);
    }
  }
}
