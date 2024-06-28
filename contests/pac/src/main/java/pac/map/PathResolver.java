package pac.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pac.Player;

public class PathResolver {
  public static final int DEPTH = 14;
  static final HashMap<Pos, List<Path>> paths = new HashMap<>();
  
  public static void build() {
    long start = System.currentTimeMillis();
    // pour chaque point vide, on construit une liste de path de depth X
    for (int i=0;i<Pos.SURFACE;i++) {
      Pos current = Pos.getFromOffset(i);
      if (current == Pos.INVALID || Player.map.isWall(current)) continue;
      
      buildPaths(current);
     }
    
    // debug
//    Pos pos = Pos.get(3, 1);
//    System.err.println("Path length "+DEPTH+" from "+pos);
//    List<Path> pathsAtPos = paths.get(pos);
//    System.err.println(" count = "+pathsAtPos.size());
//    for (Path p : pathsAtPos) {
//      System.err.println(p.positions);
//    }
    long end = System.currentTimeMillis();
    System.err.println("Time for PathResolver "+(end-start));
  }

  public static List<Path> getPathAt(Pos pos) {
    return paths.get(pos);
  }
  
  private static void buildPaths(Pos depart) {
    Pos currentPath[] = new Pos[DEPTH];
    List<Path> pathsAtPos = new ArrayList<>();
    visit(pathsAtPos, depart, currentPath, new HashSet<>(), 0);
    paths.put(depart, pathsAtPos);
  }

  private static void visit(List<Path> paths, Pos current, Pos[] currentPath, Set<Pos> visited, int depth) {
    
    if (depth == DEPTH) {
      // reconstruct path
      Path path = new Path(DEPTH);
      for (int i=0;i<DEPTH;i++) {
        path.positions[i] = currentPath[i];
      }
      paths.add(path);
      return;
    }
    
    currentPath[depth] = current;
    visited.add(current);
    
    boolean culDeSac = true;
    for (Pos neighbor : current.neighbors) {
      if (neighbor == Pos.INVALID || visited.contains(neighbor)) continue;
      culDeSac = false;
      visit(paths, neighbor, currentPath, visited, depth+1);
    }
    
    
    if (culDeSac) {
      // restart, but without visitedCells
      HashSet<Pos> newVisited = new HashSet<>();
      newVisited.add(current);
      for (Pos neighbor : current.neighbors) {
        if (neighbor == Pos.INVALID) continue;
        visit(paths, neighbor, currentPath, newVisited, depth+1);
      }
    }

    visited.remove(current);
  }
}
