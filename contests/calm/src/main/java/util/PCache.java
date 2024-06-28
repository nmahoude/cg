package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PCache {
  static int[][] emptyPos = {
      { 1, 1 }, { 2, 1 }, { 3, 1 }, { 4, 1 }, { 5, 1 }, { 6, 1 }, { 7, 1 }, { 8, 1 }, { 9, 1 },
      { 1, 2 }, { 6, 2 }, { 9, 2 },
      { 1, 3 }, { 3, 3 }, { 4, 3 }, { 6, 3 }, { 7, 3 }, { 9, 3 },
      { 1, 4 }, { 4, 4 }, { 9, 4 },
      { 1, 5 }, { 2, 5 }, { 3, 5 }, { 4, 5 }, { 5, 5 }, { 6, 5 }, { 7, 5 }, { 8, 5 }, { 9, 5 },
  };

  public static int[][] board = {
      {1,1,1,1,1,1,1,1,1,1,1},
      {1,0,0,0,0,0,0,0,0,0,1},
      {1,0,1,1,1,1,0,1,1,0,1},
      {1,0,1,0,0,1,0,0,1,0,1},
      {1,0,1,1,0,1,1,1,1,0,1},
      {1,0,0,0,0,0,0,0,0,0,1},
      {1,1,1,1,1,1,1,1,1,1,1},
  };
  
  static Map<Integer, List<P>> cache = new HashMap<>();

  static {
    P.get(0,0);
    buildPossiblesMove();
  }
  
  public static List<P> getListOfMove(P pos1, P pos2) {
    int key = pos1.index + pos2.index * 77;
    return cache.get(key);
  }
  
  private static void buildPossiblesMove() {
    for (int[] p1 : emptyPos) {
      for (int[] p2 : emptyPos) {
        if (p1 == p2) continue;
        P pos1 = P.get(p1[0], p1[1]);
        P pos2 = P.get(p2[0], p2[1]);
        int key = pos1.index + pos2.index * 77;
        
        List<P> toGo = getPlacesToGo(pos1, pos2);
        cache.put(key, toGo);
      }
    }
  }

  private static List<P> getPlacesToGo(P pos1, P pos2) {
    List<P> places = new ArrayList<>();
    places.add(pos1);
    for (int i=0;i<4;i++) {
      List<P> newPlaces = new ArrayList<>();
      newPlaces.addAll(places);
      for (P place : places) {
        addP(newPlaces, place.getUp(), pos2);
        addP(newPlaces, place.getRight(), pos2);
        addP(newPlaces, place.getDown(), pos2);
        addP(newPlaces, place.getLeft(), pos2);
      }
      places = newPlaces;
    }
    
    return places;
  }

  private static void addP(List<P> places, P newP, P pos2) {
    if (board[newP.y][newP.x] == 1) // /!\ transposition
      return; //table
    if (newP == pos2)
      return; // blocked
    if (places.contains(newP))
      return;
    places.add(newP);
  }
}
