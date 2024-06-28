package pac.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

import pac.Player;

public class Map {
  private static final int WALL = 1;
  public int cells[] = new int[Pos.SURFACE];
  public int width;
  public int height;

  public Map() {
    for (int i=0;i<Pos.SURFACE;i++) {
      cells[i] = WALL;
    }
  }
  
  private void buildPosNeighbors() {
    for (int y=0;y<height;y++) {
      for (int x=0;x<width;x++) {
        Pos currentPos = Pos.get(x, y);
        
        if (cells[currentPos.offset] == 1) continue; // no neighbors, invalids by default
        
        for (int d=0;d<4;d++) {
          
          int newX = (x + Pos._d[d][0] + width) % width;
          int newY = (y + Pos._d[d][1] + height) % height;
          Pos newPos = Pos.get(newX, newY);
          if (cells[newPos.offset] == 1) {
            currentPos.neighbors[d] = Pos.INVALID;
          } else {
            currentPos.neighbors[d] = Pos.get(newX, newY);
            currentPos.neighborsList[currentPos.neighborsListFE++] = Pos.get(newX, newY); 
          }
        }
      }
    }
    
    buildDeadEnds();
  }

  private void buildDeadEnds() {
    for (int i=0;i<Pos.SURFACE;i++) {
      Pos pos = Pos.getFromOffset(i);
      if (isWall(pos)) continue;
      
      if (pos.neighborsListFE == 1) {
        pos.deadEnd = true;
        Pos current = pos;
        Pos next = current.neighborsList[0];
        while (next.neighborsListFE == 2) {
          next.deadEnd = true;

          if (next.neighborsList[0] != current) {
            current = next;
            next = next.neighborsList[0];
          } else {
            current = next;
            next = next.neighborsList[1];
          }
        }
      }
    }
    
    // debugMap("Dead ends", pos -> pos.deadEnd ?  "E" : " ");
  }

  public void read(Scanner in) {
    width = in.nextInt();
    height = in.nextInt();
    if (Player.DEBUG_TU) {
      System.err.println("\""+width+" "+height+"\"+EOF+"   );
    }
    if (in.hasNextLine()) {
      in.nextLine();
    }
    for (int y = 0; y < height; y++) {
      String row = in.nextLine(); // one line of the grid: space " " is floor, pound "#" is wall
      if (Player.DEBUG_TU) System.err.println("\""+row+"\"+EOF+");
      for (int x = 0; x < width; x++) {
        Pos current = Pos.get(x, y);
        cells[current.offset] = (row.charAt(x) == ' ' ? 0 : 1);
      }
    }
    
    buildPosNeighbors();
    initDistances();
    buildPathResolver();
  }
  
  private void buildPathResolver() {
    PathResolver.build();
  }

  public void debugMap(String title, Function<Pos, String> fn) {
    debugMap(title, '#', fn);
  }

  public void debugMapWithoutWall(String title, Function<Pos, String> fn) {
    debugMap(title, ' ', fn);
  }

  private void debugMap(String title, char wall, Function<Pos, String> fn) {
    System.err.println(title);
    
    for (int y=0;y<height;y++) {
      for (int x=0;x<width;x++) {
        Pos current = Pos.get(x, y);
        char cell;
        if (cells[current.offset] == 1) cell = wall;
        else cell = fn.apply(current).charAt(0);

        System.err.print(cell);
      }
      System.err.println();
    }
  }

  private void initDistances() {
    for (int i = 0; i < Pos.distances.length; i++) {
      Pos.distances[i] = Integer.MAX_VALUE;
    }

    for (int y=0;y<height;y++) {
      for (int x=0;x<width;x++) {
        Pos from = Pos.get(x, y);
        if (cells[from.offset] != 0) continue;
        calculateDistancesFrom(from);
      }
    }
  }

  private void calculateDistancesFrom(Pos from) {

    List<Pos> toVisit = new ArrayList<>();
    List<Pos> visited = new ArrayList<>();
    toVisit.add(from);
    Pos.distances[from.offset * Pos.SURFACE + from.offset] = 0;

    while (!toVisit.isEmpty()) {
      Pos current = toVisit.remove(0);
      visited.add(current);
          
      for (Pos neighbor : current.neighbors) {
        if (neighbor == Pos.INVALID) continue;
        if (visited.contains(neighbor)) continue;
        
        Pos.distances[from.offset * Pos.SURFACE + neighbor.offset] = Math.min(Pos.distances[from.offset * Pos.SURFACE + neighbor.offset], Pos.distances[from.offset * Pos.SURFACE + current.offset]+1);
        toVisit.add(neighbor);
      }
    }
  }

  public boolean isWall(Pos p) {
    return cells[p.offset] == WALL;
  }
}
