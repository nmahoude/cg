package pac.ai;

import java.util.Iterator;
import java.util.List;

import pac.Player;
import pac.State;
import pac.agents.Pacman;
import pac.agents.Pellet;
import pac.map.Path;
import pac.map.PathResolver;
import pac.map.Pos;
import pac.sim.Action;
import pac.simpleai.ActionAI;

public class PathValueCalculator {
  private static final int MAX_BEST_PATHS = 200;

  private State state;
  private int maxTurn;

  Path bestPaths[][] = new Path[5][MAX_BEST_PATHS]; // work
  int bestPathsFE[] = new int[5]; // work


  public void init(State state, int maxTurn) {
    this.state = state;
    this.maxTurn = maxTurn;
    /**
     * 
     * pour chaque pacman, une liste des paths sur une certaine DEPTH avec leur score
     * on ne conserve que les XX meilleurs path
     * 
     * sur XXXX coups, on prend 1 path de chaque au hazard et on recombine 
     * on prend celui qui a le plus de pellets 
     * 
     */
    
    updateBlockedPos(true);
    
    for (int i=0;i<5;i++) {
      bestPathsFE[i] = 0;
      
      Pacman pacman = state.pacmen[i];
      if (pacman.pos == Pos.INVALID ) continue;

      preCalculateAllPaths(pacman);
    }

    updateBlockedPos(false);
  }
   
  private void updateBlockedPos(boolean value) {
    for (int o=5;o<5+state.maxPacmen;o++) {
      if (state.pacmen[o].isDead()) continue;
      
      Pos pos = Player.oracle.getPotentialPosOf(o);
      if (pos != Pos.INVALID) {
        pos.blocked = value;
      }
    }
    
  }

  public void sort() {
    for (int i=0;i<5;i++) {
      Pacman pacman = state.pacmen[i];
      if (pacman.pos == Pos.INVALID || pacman.isDead()) continue;
      
      sort(pacman);
    }    
  }
  
    private void preCalculateAllPaths(Pacman pacman) {
      List<Path> paths = PathResolver.getPathAt(pacman.pos);

      for (Path path : paths) {
        path.score = 0;

        ActionAI action;
        if (pacman.speedTurnsLeft > 0) {
          // handle special case of speed and dead end
          Pos second = pacman.pos != path.positions[2] ? path.positions[2] : path.positions[1];
          action = pacman.find(Action.MOVE, path.positions[1], second);
        } else {
          action = pacman.find(Action.MOVE, path.positions[1], path.positions[1]);
        }
        if (action.score < -7000) {
          path.score = -10_000;
          continue;
        } else if (action.score < -1000) {
          path.score = -100;
        }
        
        
        for (int depth=1;depth<maxTurn;depth++) {
          Pos pos = path.positions[depth];
          
          if (depth < 5 && pos.blocked) {
            for (;depth<maxTurn;depth++) {
              path.scores[depth] = 0;
            }
            break;
          }
          Pellet pellet = state.pellets[pos.offset];
          double localScore;
          if (pellet == null) {
            localScore = 0;
          } else {
            localScore = pellet.value / (depth); // TODO depreciation plus rapide ?
          }
          path.scores[depth] = localScore;
          path.score += localScore;
        }

      }
    }

    public void sort(Pacman pacman) {
      int index = pacman.index;
      List<Path> paths = PathResolver.getPathAt(pacman.pos);
      
      // TODO pas besoin de trier si AG ?
      paths.sort((p1, p2) -> Double.compare(p2.score, p1.score));
      Iterator<Path> iterator = paths.iterator();
      int i = 0;
      while (iterator.hasNext() && i < MAX_BEST_PATHS) {
        Path path = iterator.next();
        if (path.score < 0) continue;
        
        bestPaths[index][i++] = path;
        bestPathsFE[index]++;
      }

      if (bestPathsFE[index] == 0) {
        // no path found
        bestPaths[index][i++] = paths.get(0);
        bestPathsFE[index]++;
      }
      
      if (Player.debugBestPath(pacman)) {
        System.err.println("best paths for "+pacman.index);
        for (i=0;i<2;i++) {
          Path path = bestPaths[index][i];
          path.debug(state);
        }
      }
    }
}
