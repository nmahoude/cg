package pac.ai;

import pac.Player;
import pac.State;
import pac.map.Path;
import pac.map.Pos;

public class PathScorer {
  static long currentMarker = 0;
  static Pos oldPos1[] = new Pos[5];
  static Pos oldPos2[] = new Pos[5];
  static Pos currentPos[] = new Pos[5];
  
  static double scorePaths(State state, Path[] paths, int maxTurn) {
    currentMarker++;
    
    
    for (int i=0;i<5;i++) {
      if (state.pacmen[i].pos == Pos.INVALID ) continue;
      oldPos1[i] = state.pacmen[i].pos;
      oldPos2[i] = state.pacmen[i].pos;
      currentPos[i] = state.pacmen[i].pos;
    }
    
    double score = 0; 
    
    for (int p = 1 /* 0 is current pos */;p<maxTurn;p++) {
      for (int i=0;i<5;i++) {
        if (state.pacmen[i].pos == Pos.INVALID ) continue;
        
        Path currentPath = paths[i];
        Pos pos = currentPath.positions[p];

        if (Player.BR_ENTER_DEADEND_ON_DANGER && p == 1) {
          if (pos.deadEnd) {
            score -= 0.5;
          }
        }
        
        oldPos2[i] = oldPos1[i];
        oldPos1[i] = currentPos[i];
        currentPos[i] = pos;

        // cheap unique positions checker
        if (pos.marker != currentMarker) {
          score+=currentPath.scores[p];
          pos.marker = currentMarker;
        }
        
        if (Player.BR_AVOID_CROSSING) {
          // detect hard blocking ?
          // if old-2 == current for each pacman, it's a blocking cross
          if (p < 3) {
            for (int o=0;o<5;o++) {
              if (o == i || state.pacmen[o].pos == Pos.INVALID ) continue;
              if (oldPos2[i] == currentPos[o] && oldPos2[o] == currentPos[i]) return Double.NEGATIVE_INFINITY; // pas de croisement 2 à 2
            }
          }
        }
        
      }
//      if (p == 300) {
//        int totalDistance = 0;
//        for (int i=0;i<5;i++) {
//          if (state.pacmen[i].pos == Pos.INVALID ) continue;
//          for (int o=i+1;o<5;o++) {
//            if (state.pacmen[o].pos == Pos.INVALID ) continue;
//            totalDistance+= state.pacmen[o].pos.distance(state.pacmen[i].pos);
//          }
//        }
//        score += totalDistance / 100000.0;
//      }
      
    }
    
//    int totalPellets = 0;
//    double total = 0;
//    for (int i=0;i<5;i++) {
//      if (state.pacmen[i].pos == Pos.INVALID ) continue;
//      for (int offset=0;offset<Pos.SURFACE;offset++) {
//        Pos pos = Pos.getFromOffset(offset);
//        if (pos.marker != currentMarker) {
//          total += 1.0 / pos.distance(currentPos[i]);
//          totalPellets++;
//        }
//      }
//    }
//
//    score += total / totalPellets;
    
    return score;
  }
}
