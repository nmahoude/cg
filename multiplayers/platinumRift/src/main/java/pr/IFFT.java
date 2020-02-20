package pr;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IFFT {
  private Grid grid;
  String moveOrders;
  String dropOrders;
  private int nbPods;

  public IFFT(Grid grid) {
    this.grid = grid;
  }

  private void reset() {
    nbPods = Player.platinum / 20;
    moveOrders = "";
    dropOrders = "";
  }

  public void think() {
    reset();

    precalculate();

    move();

    drop();

  }

  private void precalculate() {
    assessThreat();
  }

  private void assessThreat() {
    for (Cell cell : grid.world) {
      cell.threats.clear();
      cell.threathen.clear();
      cell.threatCount = 0;
    }
    for (Cell cell : grid.world) {
      if (!cell.isMine())
        continue;

      int sumEnnemies = cell.neighbors().stream()
          .filter(Cell::isEnnemy)
          .mapToInt(v -> v.podCount)
          .sum();
      if (sumEnnemies > cell.podCount) {
        cell.threatCount = sumEnnemies - cell.podCount;
        cell.neighbors().stream()
            .filter(v -> v.isEnnemy() && v.podCount > 0)
            .forEach(v -> {
              v.threathen.add(cell);
              cell.threats.add(v);
            });
      }
    }
  }

  private void move() {
    List<Cell> myCells = grid.world.stream()
        .filter(Cell::isMine)
        .collect(Collectors.toList());

    for (Cell cell : myCells) {
      System.err.println("Calculation Moves from " + cell.id);
      int size = cell.myPods();
      // Verifier si il faut laisser des troupes
      int sumEnnemies = cell.neighbors().stream()
          .filter(Cell::isEnnemy)
          .mapToInt(v -> v.podCount)
          .sum();

      if (sumEnnemies > 0 && cell.platinum > 0) {
        // maybe we can attack a better cell !
        boolean dontDefend = false;
        for (Cell n : cell.neighbors()) {
          if (n.platinum >= cell.platinum && cell.podCount >= n.podCount) {
            dontDefend = true;
          }
        }
        if (!dontDefend) {
          System.err.println("Defending");
          size = Math.max(0, size - sumEnnemies);
        }
      }

      // verifier si on peut aider une voisine en detresse
      List<Cell> neighborsByPlatinum = cell.neighbors().stream().sorted(Grid.platinumDESC).collect(Collectors.toList());
      for (Cell v : neighborsByPlatinum) {
        if (v.threathen.isEmpty())
          continue;
        System.err.println("Helping sister in distress ");
        movePod(size, cell, v);
        size = 0;
        break;
      }

      List<Cell> potentialAttack = cell.neighbors().stream()
          .sorted(Grid.platinumDESC)
          
          .filter(n -> n.isEnnemy() && n.isEmpty())
          .collect(Collectors.toList());
      for (Cell voisine : potentialAttack) {
        if (voisine.podCount < size) {
          movePod(size - voisine.podCount, cell, voisine);
          size -= voisine.podCount;
        }
        if (size <= 0)
          break;
      }

      List<Cell> potential = cell.neighbors().stream()
          .sorted(Grid.platinumDESC)
          .filter(n -> n.isEmpty())
          .filter(n -> !n.isMine())
          .collect(Collectors.toList());
      for (Cell neighbor : potential) {
        if (size <= 0)
          break;
        movePod(1, cell, neighbor);
        size--;
      }

      if (size > 0) {
        Cluster cluster = cell.cluster;
        Set<Cell> frontier = cluster.frontier;

        System.err.println("Try to go to frontier, frontier set is : ");
        for (Cell t : frontier) {
          System.err.print("" + t.id + ", ");
        }
        System.err.println();

        // find the neighbors cell that bring me closest to frontier
        int min = Integer.MAX_VALUE;
        Cell best = null;
        for (Cell v : cell.neighbors()) {
          for (Cell f : frontier) {
            if (grid.distances[v.id][f.id] < min) {
              min = grid.distances[v.id][f.id];
              best = v;
            }
          }
        }
        if (best != null) {
          System.err.println("  found the best neighbor to go to frontied : " + best.id + " with min= " + min);
          movePod(size, cell, best);
        } else {
          // set with no frontier
          movePod(size, cell, cell);
        }
      }

      if (size > 0) {
        System.err.println("All actions evaluated, still some pods");
      }
    }
  }

  private void movePod(int size, Cell from, Cell target) {
    moveOrders += " " + size + " " + from.id + " " + target.id;
  }

  private void dropPod(int size, Cell target) {
    dropOrders += " " + size + " " + target.id;
    nbPods -= size;
  }

  private void drop() {
    if (Player.playerCount >= 3 && Player.round == 1) {
      nbPods-=2; // keep 2 pods for next round
    }
    if (Player.round == -1) {
      if (Player.playerCount >= 4) {
        do4playersStrategy();
        return; // do nothing at first turn if too many players
      }
      int i = 0;
      if (Player.playerCount >= 4)
        i = 1;
      while (nbPods > 0) {
        if (grid.world.get(i).platinum < 4)
          i = 0;
        dropPod(Player.playerCount > 2 ? 3 : 2, grid.world.get(i++));
        if (nbPods <= 0)
          break;
      }
    } else {

      int lastNbPods = -1;
      while (nbPods > 0 && lastNbPods != nbPods) {
        lastNbPods = nbPods;

        // defend my attacked cells
        for (Cell cell : grid.world) {
          if (!cell.isMine())
            continue;

          // check voisine pour voir la menace
          // TODO attack is the best defense ?
          int sumEnnemies = cell.neighbors().stream()
              .filter(Cell::isEnnemy)
              .mapToInt(v -> v.podCount)
              .sum();
          if (sumEnnemies == 0)
            continue; // no threat in the neighboring

          if (sumEnnemies <= cell.podCount)
            continue; // assume no threat ?
          
          if (cell.platinum == -1) {
            // assume no thread if no platinium ?
            // TODO protect frontier ?
            continue; 
          }
          
          if (nbPods >= (sumEnnemies - cell.podCount)) {
            int size = sumEnnemies - cell.podCount;
            System.err.println("Dropping " + size + " pods to defend cell " + cell.id);
            dropPod(size, cell);
          } else {
            System.err.println("Cell " + cell.id + " is attacked but can't defend directly");
          }
        }

        if (nbPods > 0 && Player.playerCount > 2) {
          conquerSmallContinents();
        }

        if (nbPods > 0) {
          System.err.println("Going for grabbing free spots");
          // grab the empty platinum spots
          for (Cell cell : grid.world) {
            if (nbPods == 0)
              break;
            if (!cell.isNeutral())
              continue;
            if (cell.platinum == 0)
              break;
            System.err.println("Grab empty spot " + cell.id);
            dropPod(1, cell);
          }
        }

        if (nbPods > 0) {
          // find all big cells not protected
          for (Cell cell : grid.world) {
            if (cell.isEnnemy() && cell.podCount == 0) {
              System.err.println("found cell " + cell.id + " unprotected ");
              Cell best = cell.neighbors().stream()
                  .sorted(Grid.platinumDESC)
                  .filter(c -> c.isMine() || c.isNeutral())
                  .findFirst().orElse(null);
              if (best != null) {
                System.err.println("Found a target to attack it");
                dropPod(nbPods, best);
                if (nbPods <= 0) {
                  break;
                }
              } else {
                System.err.println("Didn't found a target to attack it");
              }
            }
          }
        }

        break;
      }
    }
  }

  private void conquerSmallContinents() {
    // check antartica
    if (!grid.antartica.isOwned() && grid.antartica.hasNeutral() && grid.antartica.totalPodsCount() == 0) {
      Cell cell = grid.antartica.cells.stream()
          .sorted(Grid.platinumDESC)
          .filter(c -> c.canDrop())
          .findFirst().orElse(null);

      if (cell != null) {
        dropPod(1, cell);
      }
    }
    
    if (!grid.japan.isOwned() && grid.japan.hasNeutral() && grid.japan.totalPodsCount() == 0) {
      Cell cell = grid.japan.cells.stream()
          .sorted(Grid.platinumDESC)
          .filter(c -> c.canDrop())
          .findFirst()
          .orElse(null);

      if (cell != null) {
        dropPod(1, cell);
      }
    }
  }

  private void invadeSmallContinents() {
    // try to invade japan & antartica if it worth it !
    
    invadeContinents(grid.japan.cells, 3, 1);
    invadeContinents(grid.japan.cells, 8, 2);
    invadeContinents(grid.antartica.cells, 5, 1);
    invadeContinents(grid.antartica.cells, 8, 1);
  }

  private void do4playersStrategy() {
    
  }

  private boolean invadeContinents(Set<Cell> cells, int threshold, int units) {
    int totalPlat = cells.stream().mapToInt(c ->c.platinum).sum();
    if (totalPlat >= threshold) {
      List<Cell> orderedCells = cells.stream().sorted(Grid.platinumDESC).collect(Collectors.toList());
      dropPod(units, orderedCells.get(0));
      return true;
    }
    return false;
  }

  public void output() {
    System.out.println(moveOrders);
    System.out.println(dropOrders);
  }

}
