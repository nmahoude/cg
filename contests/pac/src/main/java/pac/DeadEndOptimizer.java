package pac;

import java.util.ArrayList;
import java.util.List;

import pac.agents.Pacman;
import pac.map.Pos;

public class DeadEndOptimizer {
  static Pos allCulDeSac[][][] = new Pos[5][4][20];
  static int allCulDeSacFE[][] = new int[5][4];
  private State state;

  
  public DeadEndOptimizer(State state) {
    this.state = state;
  }

  public void clear() {
    for (int i=0;i<5;i++) {
      for (int d=0;d<4;d++) {
        allCulDeSacFE[i][d] = 0;
      }
    }
  }
  
  public void search(Pacman pacman, Pos last, int dir) {
    if (last.deadEnd) {
      // on repart dans l'autre sens
      int invD = invDir(dir);
      while (last.deadEnd) {
        if (last == pacman.pos) {
          allCulDeSacFE[pacman.index][dir] = 0;
          break;
        }
        allCulDeSac[pacman.index][dir][allCulDeSacFE[pacman.index][dir]++] = last;
        last = last.neighbors[invD];
      }
    }
  }

  private int invDir(int d) {
    switch(d) {
      case 0 : return 2;
      case 1 : return 3;
      case 2 : return 0;
      case 3 : return 1;
    }
    return -1;
  }

  public void optimize() {
    for (int i=0;i<5;i++) {
      Pos pacman = state.pacmen[i].pos;
      if (pacman == Pos.INVALID) continue;
      for (int d=0;d<4;d++) {
        if (Player.DEBUG_DEADENDOPTIMIZER &&  allCulDeSacFE[i][d] == 1) {
          System.err.println("#"+i+" -> CulDeSac découvert : mais taille 1, on ne peut rien en faire");
        }
        if (allCulDeSacFE[i][d] > 1) {
          if (Player.DEBUG_DEADENDOPTIMIZER) System.err.println("#"+i+" -> CulDeSac découvert : checking pellets");
          boolean empty = true;
          for (int cds=0;cds<allCulDeSacFE[i][d];cds++) {
            Pos current = allCulDeSac[i][d][cds];
            if (state.pellets[current.offset].value > 0) {
              empty = false;
            }
          }
          if (empty) {
            if (Player.DEBUG_DEADENDOPTIMIZER) System.err.println("Cul de sac is empty, reducing all pellets value in it");
            fillCulDeSac(allCulDeSac[i][d][0], 0.0005);
          } else {
            if (Player.DEBUG_DEADENDOPTIMIZER) {
              System.err.println("Cul de sac contains pellet, donc toutes les pelletes sont encore dedans ! ");
              System.err.println("Starting at "+allCulDeSac[i][d][0]);
            }
            fillCulDeSac(allCulDeSac[i][d][0], 1.0);
          }
        }
      }
    }
  }
  
  List<Pos> visited = new ArrayList<>(30);
  private void fillCulDeSac(Pos pos, double value) {
    visited.clear();
    visited.add(pos);

    fillOneCulDeSac(pos, visited, value);
  }
  

  private void fillOneCulDeSac(Pos current, List<Pos> visited, double value) {
    for (Pos p : current.neighbors) {
      if (p.deadEnd && p != Pos.INVALID && !visited.contains(p)) {
        visited.add(p);
        if (state.pellets[p.offset].value > 0) {
          if (value == 1.0) {
            state.pellets[p.offset].value = Math.max(state.pellets[p.offset].value, value);
          }
          if (Player.DEBUG_DEADENDOPTIMIZER) System.err.println("set pellets "+p+" at value "+value);
        }
        fillOneCulDeSac(p, visited, value);
      }
    }
  }
}
