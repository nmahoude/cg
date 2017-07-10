package tge.minimax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import tge.Agent;
import tge.Cell;
import tge.Player;
import tge.Point;
import tge.WallOrientation;
import tge.paths.AStar;
import tge.simulation.Action;
import tge.simulation.ActionType;

public class Node {
  
  int depth;
  public Action action;
  public double score;

  public Node(int depth) {
    this.depth = depth;
  }

  public double evaluate() {
    List<Cell> path1 = AStar.astar(Player.grid.get(Player.agents[myId()].position), myId());
    int dist1 = path1.size() -1 ;
    List<Cell> path2 = AStar.astar(Player.grid.get(Player.agents[hisId()].position), hisId());
    int dist2 = path2.size() -1 ;
    System.err.println(""+action.toOutput()+" ==> d : " + dist1 + " vs "+dist2);

    if (dist1 == -1) return Double.NEGATIVE_INFINITY;
    if (dist2 == -1) return Double.NEGATIVE_INFINITY;
    
    double distScore = - (dist1 - dist2);
    if (action.type == ActionType.MOVE) {
      distScore +=0.5;
    }
    if (dist1 == 0) return 1_000_000;
    if (dist2 == 1) distScore -=1_000_000;

    return  distScore ;
  }

  int myId() {
    return Player.myId;
  }
  int hisId() {
    int i = (Player.myId+1) % Player.playerCount;
    while (Player.agents[i].position == Point.unknown) {
      i = (i+1) % Player.playerCount;
    }
    return i;
  }
  public List<Node> getChildren() {
    Agent agent;
    if (depth %2 == 0) {
      // my moves
      agent = Player.agents[myId()];
    } else {
      // opponent moves
      agent = Player.agents[hisId()];
    }
    
    List<Node> nodes = getMoves(agent);
    if (agent.wallLefts > 0) {
      nodes.addAll(getPossibleWalls(agent));
    }
    return nodes;
  }

  private Collection<? extends Node> getPossibleWalls(Agent agent) {
    List<Node> nodes = new ArrayList<>();

    for (int y=0;y<9;y++) {
      for (int x=0;x<9;x++) {
        if (x!=0) {
          Action action = new Action();
          action.agent = agent;
          action.type = ActionType.WALL;
          action.orientation = WallOrientation.VERTICAL;
          action.position = Point.get(x,  y);
          Node node = new Node(this.depth+1);
          node.action = action;
          node.score = 0.0;
          nodes.add(node);
        }
        if (y != 0) {
          Action action = new Action();
          action = new Action();
          action.agent = agent;
          action.type = ActionType.WALL;
          action.orientation = WallOrientation.HORIZONTAL;
          action.position = Point.get(x,  y);
          Node node = new Node(this.depth+1);
          node = new Node(this.depth+1);
          node.action = action;
          node.score = 0.0;
          nodes.add(node);
        }
      }
    }

    return nodes;
  }

  private List<Node> getMoves(Agent agent) {
    Cell cell = Player.grid.get(agent.position);

    List<Node> nodes = new ArrayList<>();
    for (int i=0;i<4;i++) {
      if (cell.walls[i] != 0 ) continue;
      Action action = new Action();
      action.agent = agent;
      action.type = ActionType.MOVE;
      action.moveIndex = i;
      Node node = new Node(this.depth+1);
      node.action = action;
      node.score = 0.0;
      nodes.add(node);
    }
    return nodes;
  }

}
