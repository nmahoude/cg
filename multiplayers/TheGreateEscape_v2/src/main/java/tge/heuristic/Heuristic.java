package tge.heuristic;

import java.util.ArrayList;
import java.util.List;

import tge.Agent;
import tge.Cell;
import tge.Grid;
import tge.Player;
import tge.Point;
import tge.WallOrientation;
import tge.paths.AStar;
import tge.paths.FloodFill;
import tge.simulation.Action;
import tge.simulation.ActionType;
import tge.simulation.Simulation;

public class Heuristic {
  Simulation simulation = new Simulation();
  List<Node> nodes = new ArrayList<>();

 public Action getBestAction() {
   long start = System.currentTimeMillis();
   if (Player.round <= 4) {
     System.err.println("Round is "+Player.round);
     Action action = null;
     if (Player.myId == 0 ) {
       if (Player.me.position.y < 2 || Player.me.position.y > 6) {
         int x  = 2 * (Player.round-1);
         int y = Player.me.position.y > 4 ? Player.me.position.y : Player.me.position.y+1;
         action = createActionWall(x, y, WallOrientation.HORIZONTAL);
       }
     } else if (Player.myId == 1 ) {
       if (Player.me.position.y < 2 || Player.me.position.y > 6) {
         int x  = 2 * (Player.round-1);
         int y = Player.me.position.y > 4 ? Player.me.position.y : Player.me.position.y+1;
         action = createActionWall(7-x, y, WallOrientation.HORIZONTAL);
       }
     } else {
       if (Player.me.position.x < 2 || Player.me.position.x > 6) {
         int x = Player.me.position.x > 4 ? Player.me.position.x : Player.me.position.x+1;
         int y  = 2 * (Player.round-1);
         action = createActionWall(x, y, WallOrientation.VERTICAL);
       }
     }
     if (action != null && simulation.play(action)) {
       int dist[] = getDistances();
       boolean canPutWall =true;
       for (int i=0;i<3;i++) {
         if (dist[i] == -100) continue;
         if (dist[i] == -1) canPutWall = false;
       }
       simulation.unplay(action);
       if(canPutWall) {
         return action;
       } else {
         System.err.println("cant put wall, would block somebody");
       }
     } else {
       System.err.println("Can't put blocker wall, already a wall");
     }
   }
   
   
   getMoves(Player.me);
   if (Player.me.wallLefts > 0) {
     getPossibleWalls(Player.me);
   }
   
   double maxScore = Double.NEGATIVE_INFINITY;
   Node bestNode = null;
   Node bestMove = null;
   while (!nodes.isEmpty()) {
     Node node = nodes.remove(0);
     if (node.bonus > 0) System.err.println("Using bonus nodes :" + node.action.toOutput());
     if (!simulation.play(node.action)) {
       continue;
     }
     double score = evaluate(node);
     simulation.unplay(node.action);

     if (score > maxScore) {
       if (node.action.type == ActionType.MOVE) {
         bestMove = node;
       }
       maxScore = score;
       bestNode = node;
     }
   }

   long end= System.currentTimeMillis();
   System.err.println("Time of heuristic "+(end-start));
   if (false && Player.me.clearPath && Player.me.currentPath.size() <= Player.bandit.currentPath.size()) {
     return bestMove.action;
   } else {
     return bestNode.action;
   }
 }
 
 private Action createActionWall(int x, int y, WallOrientation vertical) {
   Action action = new Action();
   action.agent = Player.me;
   action.type = ActionType.WALL;
   action.orientation = vertical;
   action.position = Point.get(x, y);
  return action;
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
 public double evaluate(Node node) {
   int dist1 = -1;
   int dist2 = -1;
   int dist[] = getDistances();
   for (int i=0;i<3;i++) {
     if (dist[i] == -100) continue;
     if (dist[i] == -1) return Double.NEGATIVE_INFINITY;
     if (i == Player.myId) dist1 = dist[i];
     if (i == Player.bandit.id) dist2 = dist[i];
   }
   
   if (node.action.type == ActionType.WALL && dist1 > Player.me.currentPath.size()-1) {
     Player.me.clearPath = false;
   }
   if (node.parentNode == null && node.action.type == ActionType.WALL 
       && dist1 / Player.me.currentPath.size() >= 2) {
     System.err.println("Adding bonus nodes, parentNode is " + node.parentNode + " and bonus : " + node.bonus);
     System.err.println("Adding bonus nodes, initial dist : "+Player.me.currentPath+" , new dist : "+dist1 );
     // reput in the node list the actions that can block this wall and give them a bonus
     createBlockingWallsNode(node);
     
   }
   if (dist1 == 0)  { node.score = 1_000_000; return node.score; }
   if (dist2 == 1)  { node.score = -1_000_000; return node.score; }

   
   double distScore = - (0.8*dist1 - dist2);

   if (node.parentNode != null) {
     if (!simulation.play(node.parentNode.action)) {
       System.err.println("Old wall is not possible anymore ! +5 for gryfondor");
       distScore += 5;
     } else {
       int distances[] = getDistances();
       if (distances[0] == -1 || distances[1] == -1 || distances[2] == -1) {
         System.err.println("Old wall is not possible because it would block somebody +5 for gryfondor");
         distScore+= 5;
       }
       simulation.unplay(node.parentNode.action);
     }
   }
   

   
//   if (dist2 - Player.agents[hisId()].currentDist > 2) {
//     if (node.action.position.manathan(Player.agents[hisId()].position) > 2) {
//       distScore -= 3*dist2;
//     }
//   }

   if (Player.me.currentPath.size() > Player.bandit.currentPath.size()) {
//     if (node.action.type == ActionType.WALL) {
//       distScore+=0.5;
//     }     
   } else {
   }
   if (node.action.type == ActionType.MOVE) {
     distScore += 1.0;
   }
//   if (node.action.type == ActionType.WALL) {
//     if ((dist2 - Player.agents[hisId()].currentDist > 4) 
//         && distToWall(node.action, Player.agents[hisId()].position) > 0) {
//       distScore-=1_000_000;
//     }
//   }   
   
   if (node.action.type == ActionType.WALL) {
     if (distToWall(node.action, Player.bandit.position) > 0) {
       distScore -= 1.5;
     }
   }
   if (node.action.type == ActionType.WALL) {
     if (node.action.orientation == WallOrientation.HORIZONTAL && (node.action.position.x == 6 || node.action.position.x == 1)) {
       distScore -= 0.5;
     }
     if (node.action.orientation == WallOrientation.VERTICAL && (node.action.position.y == 6 || node.action.position.y == 1)) {
       distScore -= 0.5;
     }
     //distScore -= 0.1 * action.position.manathan(Player.agents[hisId()].position);
   }
   
   if (node.action.type == ActionType.WALL) {
     if (Player.me.currentPath.size() < Player.bandit.currentPath.size()) distScore -= 10;

     int count1 = new FloodFill().floodFillFromExit_dfs(Player.myId);
     int count2 = new FloodFill().floodFillFromExit_dfs(Player.bandit.id);
     if (Player.me.currentMax - count1 > 20 
         && node.action.position.manathan(Player.me.position) < 3) {
       distScore += 2;
     }
     if (Player.bandit.currentMax - count2 > 20) {
       // he can cut it's remaining cells !
//       distScore -= 1_000;
//       if (node.parentNode == null) {
//         createBlockingWallsNode(node);
//       }
     }
     //System.err.println("FF : "+ " me= "+count1+" vs "+count2);
   }
   System.err.println("" + node.action.toOutput() + " ==> "+distScore +" bonus : "+node.bonus);

   node.score = distScore + node.bonus;
   return node.score;
 }

private void createBlockingWallsNode(Node node) {
  if (node.action.orientation == WallOrientation.HORIZONTAL) {
     createWallNode(node, node.action.position.x  , node.action.position.y-1, WallOrientation.VERTICAL);
     createWallNode(node, node.action.position.x+1, node.action.position.y-1, WallOrientation.VERTICAL);
     createWallNode(node, node.action.position.x+2, node.action.position.y-1, WallOrientation.VERTICAL);

     createWallNode(node, node.action.position.x  , node.action.position.y-1, WallOrientation.HORIZONTAL);
     createWallNode(node, node.action.position.x-1, node.action.position.y  , WallOrientation.HORIZONTAL);
     createWallNode(node, node.action.position.x+1, node.action.position.y  , WallOrientation.HORIZONTAL);
     createWallNode(node, node.action.position.x  , node.action.position.y+1, WallOrientation.HORIZONTAL);
   } else {
     createWallNode(node, node.action.position.x-1, node.action.position.y  , WallOrientation.HORIZONTAL);
     createWallNode(node, node.action.position.x-1, node.action.position.y+1, WallOrientation.HORIZONTAL);
     createWallNode(node, node.action.position.x-1, node.action.position.y+2, WallOrientation.HORIZONTAL);

     createWallNode(node, node.action.position.x-1, node.action.position.y, WallOrientation.VERTICAL);
     createWallNode(node, node.action.position.x, node.action.position.y-1, WallOrientation.VERTICAL);
     createWallNode(node, node.action.position.x, node.action.position.y+1, WallOrientation.VERTICAL);
     createWallNode(node, node.action.position.x+1, node.action.position.y, WallOrientation.VERTICAL);
   }
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

private void createWallNode(Node parent, int x, int y, WallOrientation orientation) {
   if (x < 0 || x>8 || y<0 || y>8) return;
   if (x == 0 && orientation == WallOrientation.VERTICAL) return;
   if (y == 0 && orientation == WallOrientation.HORIZONTAL) return; 
   Point point = Point.get(x, y);
   
   Action action = new Action();
   action.agent = Player.me;
   action.type = ActionType.WALL;
   action.orientation = orientation;
   action.position = point;
   Node bonusNode = new Node();
   bonusNode.action = action;
   bonusNode.bonus = 1.0;
   bonusNode.parentNode = parent;
   nodes.add(bonusNode);
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
