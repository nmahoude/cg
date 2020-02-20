package tge.heuristic;

import java.util.List;

import tge.Cell;
import tge.Player;
import tge.Point;
import tge.WallOrientation;
import tge.paths.AStar;
import tge.paths.FloodFill;
import tge.simulation.Action;
import tge.simulation.ActionType;

public class Node {

  public Action action;
  public double bonus = 0.0;
  
  public double score = Double.NEGATIVE_INFINITY;
  public int distances[] = new int[3];
  public int floodfill[] = new int[3];
  public Node parentNode = null;
}
