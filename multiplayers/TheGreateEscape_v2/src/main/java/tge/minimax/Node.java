package tge.minimax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tge.Agent;
import tge.Cell;
import tge.Player;
import tge.Point;
import tge.WallOrientation;
import tge.paths.AStar;
import tge.paths.AStarLength;
import tge.paths.FloodFill;
import tge.simulation.Action;
import tge.simulation.ActionType;

public class Node {

  int depth;
  public Action action;
  public double score;

  public Node(int depth) {
    this.depth = depth;
  }

  public double evaluate3() {
    int dist1 = -1;
    int dist2 = -1;
    for (int i = 0; i < Player.playerCount; i++) {
      if (Player.agents[i].position == Point.unknown)
        continue;
      List<Cell> path1 = AStar.astar(Player.grid.get(Player.agents[i].position), i);
      int dist = path1.size() - 1;
      if (dist == -1)
        return Double.NEGATIVE_INFINITY;
      if (i == Player.myId)
        dist1 = dist;
      if (i == hisId())
        dist2 = dist;
    }

    double score = 0.0;
    if (dist1 == 0)
      return 1_000_000;
    if (dist2 == 1)
      return -1_000_000;
    if (action.type == ActionType.WALL) {
      // score += Player.agents[Player.myId].wallLefts;
    }
    score += 4 * (dist2 - dist1);
    int count1 = 0;
    int count2 = 0;
    if (dist1 > dist2) {
      count1 = countPossibleCells(Player.myId);
      count2 = countPossibleCells(hisId());
      score += (count2 - count1);
    }
    System.err.println("" + action.toOutput() + " => " + score + " ( " + count1 + " / " + dist1 + " vs " + count2 + " / " + dist2 + ")");
    return score;
  }

  static int countPossibleCells(int id) {

    Set<Cell> possible = getPossibleCells(id);
    return possible.size();
  }

  private static Set<Cell> getPossibleCells(int id) {
    Set<Cell> possible = new HashSet<>();
    Set<Cell> visited = new HashSet<>();
    for (int y = 0; y < 9; y++) {
      for (int x = 0; x < 9; x++) {
        Cell cell = Player.grid.cells[x][y];
        if (visited.contains(cell))
          continue;
        visited.add(cell);
        List<Cell> pathToDragon = AStar.astar(cell, Player.grid.get(Player.agents[id].position));
        List<Cell> pathToExit = AStar.astar(cell, id, pathToDragon);
        if (pathToDragon.isEmpty())
          continue;
        if (pathToExit.isEmpty())
          continue;

        visited.addAll(pathToDragon);
        visited.addAll(pathToExit);

        possible.addAll(pathToDragon);
        possible.addAll(pathToExit);
      }
    }
    return possible;
  }

  public double evaluate() {
    int dist1 = -1;
    int dist2 = -1;
    for (int i = 0; i < Player.playerCount; i++) {
      if (Player.agents[i].position == Point.unknown)
        continue;
      List<Cell> path1 = AStar.astar(Player.grid.get(Player.agents[i].position), i);
      int dist = path1.size() - 1;
      if (dist == -1)
        return Double.NEGATIVE_INFINITY;
      if (i == Player.myId)
        dist1 = dist;
      if (i == hisId())
        dist2 = dist;
    }

    double score = dist2 - dist1;
    if (Player.agents[Player.myId].wallLefts > 0) {
      int myMaxPath = getMaximalPath_iteration2(Player.myId);
      int hisMaxPath = 0; // getMaximalPath_iteration2(hisId());
      System.err.println("" + action.toOutput() + " maxPath : " + myMaxPath + " vs " + hisMaxPath);
      score += 0.5 * (hisMaxPath - myMaxPath);
    }
    return score;
  }

  public static int getMaximalPath_iteration2(int id) {
    List<Cell> cellsToTest = new ArrayList<>();
    List<Cell> testedCells = new ArrayList<>();
    int cellCount = 0;
    cellsToTest.add(Player.grid.get(Player.agents[id].position));

    while (!cellsToTest.isEmpty()) {
      Cell currentCell = cellsToTest.remove(0);
      if (Cell.heuristicLength(currentCell, id) == 0)
        continue;
      if (testedCells.contains(currentCell))
        continue;
      testedCells.add(currentCell);
      List<Cell> dragonToCurrent= AStar.astar(Player.grid.get(Player.agents[id].position), currentCell);
      int pathToExit = (int)AStarLength.astar(currentCell, id, dragonToCurrent);
      if (dragonToCurrent.size() <= 0 || pathToExit<=0)
        continue;
      cellCount++;
      if (currentCell.walls[Cell.UP] == 0 && currentCell.cells[Cell.UP] != Cell.invalid)
        cellsToTest.add(currentCell.cells[Cell.UP]);
      if (currentCell.walls[Cell.DOWN] == 0 && currentCell.cells[Cell.DOWN] != Cell.invalid)
        cellsToTest.add(currentCell.cells[Cell.DOWN]);
      if (currentCell.walls[Cell.RIGHT] == 0 && currentCell.cells[Cell.RIGHT] != Cell.invalid)
        cellsToTest.add(currentCell.cells[Cell.RIGHT]);
      if (currentCell.walls[Cell.LEFT] == 0 && currentCell.cells[Cell.LEFT] != Cell.invalid)
        cellsToTest.add(currentCell.cells[Cell.LEFT]);
    }
    return cellCount;
  }

  private int getMaximalPath(int id) {
    Cell myCell = Player.grid.get(Player.agents[id].position);
    Set<Cell> biPathCells = new HashSet<>();
    Set<Cell> impossibleCells = new HashSet<>();
    for (int y = 0; y < 9; y++) {
      for (int x = 0; x < 9; x++) {
        Cell cell = Player.grid.get(Point.get(x, y));
        if (cell == myCell)
          continue;
        if (biPathCells.contains(cell))
          continue;
        if (impossibleCells.contains(cell))
          continue;
        List<Cell> path = AStar.astar(cell, myCell);
        if (path.isEmpty()) {
          impossibleCells.add(cell);
          continue;
        }
        List<Cell> path2 = AStar.astar(cell, id, path);
        if (path2.isEmpty()) {
          impossibleCells.addAll(path);
          continue;
        }

        biPathCells.addAll(path);
      }
    }
    return biPathCells.size();
  }

  public double evaluate_old() {
    int dist1 = -1;
    int dist2 = -1;
    for (int i = 0; i < Player.playerCount; i++) {
      if (Player.agents[i].position == Point.unknown)
        continue;
      List<Cell> path1 = AStar.astar(Player.grid.get(Player.agents[i].position), i);
      int dist = path1.size() - 1;
      if (dist == -1)
        return Double.NEGATIVE_INFINITY;
      if (i == Player.myId)
        dist1 = dist;
      if (i == hisId())
        dist2 = dist;
    }

    double distScore = - (1.8*dist1 - dist2);
    if (action.type == ActionType.MOVE) {
      distScore += 1.0;
    }
    if (dist1 == 0)
      return 1_000_000;
    if (dist2 == 1)
      distScore -= 1_000_000;
    if (action.type == ActionType.WALL) {
      if (action.orientation == WallOrientation.HORIZONTAL && (action.position.x == 6 || action.position.x == 1)) {
        distScore -= 0.5;
      }
      if (action.orientation == WallOrientation.VERTICAL && (action.position.y == 6 || action.position.y == 1)) {
        distScore -= 0.5;
      }
      //distScore -= 0.1 * action.position.manathan(Player.agents[hisId()].position);
    }
    
    
    if (false && action.type == ActionType.WALL) {
      int count1 = 0; //FloodFill.floodFillFromExit(Player.myId);
      int count2 = 0; //FloodFill.floodFillFromExit(hisId());
      if (Player.agents[Player.myId].currentMax - count1 > 20 
          && action.position.manathan(Player.agents[Player.myId].position) < 3) {
        distScore += 2;
      }
      System.err.println("" + action.toOutput() + " ==> "+distScore + " (ff="+count1+" vs "+count2+")");
    }

    return distScore;
  }

  int myId() {
    return Player.myId;
  }

  int hisId() {
    int i = (Player.myId + 1) % Player.playerCount;
    while (Player.agents[i].position == Point.unknown) {
      i = (i + 1) % Player.playerCount;
    }
    return i;
  }

  public List<Node> getChildren() {
    Agent agent;
    if (depth % 2 == 0) {
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

    for (int y = 0; y < 9; y++) {
      for (int x = 0; x < 9; x++) {
        if (x != 0) {
          Action action = new Action();
          action.agent = agent;
          action.type = ActionType.WALL;
          action.orientation = WallOrientation.VERTICAL;
          action.position = Point.get(x, y);
          Node node = new Node(this.depth + 1);
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
          action.position = Point.get(x, y);
          Node node = new Node(this.depth + 1);
          node = new Node(this.depth + 1);
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
    for (int i = 0; i < 4; i++) {
      if (cell.walls[i] != 0)
        continue;
      Action action = new Action();
      action.agent = agent;
      action.type = ActionType.MOVE;
      action.moveIndex = i;
      Node node = new Node(this.depth + 1);
      node.action = action;
      node.score = 0.0;
      nodes.add(node);
    }
    return nodes;
  }

}
