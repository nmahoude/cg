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
      if (Player.DEBUG_CALCULATION) {
        System.err.println("Calculation Moves from " + cell.id);
      }
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
          if (Player.DEBUG_CALCULATION) {
            System.err.println("Defending");
          }
          size = Math.max(0, size - sumEnnemies);
        }
      }

      // verifier si on peut aider une voisine en detresse
      List<Cell> neighborsByPlatinum = cell.neighbors().stream().sorted(Grid.platinumDESC).collect(Collectors.toList());
      for (Cell v : neighborsByPlatinum) {
        if (v.threathen.isEmpty())
          continue;
        if (Player.DEBUG_CALCULATION) {
          System.err.println("Helping sister in distress ");
        }
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

        if (Player.DEBUG_CALCULATION) {
          System.err.println("Try to go to frontier, frontier set is : ");
          for (Cell t : frontier) {
            System.err.print("" + t.id + ", ");
          }
          System.err.println();
        }

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
          if (Player.DEBUG_CALCULATION) {
            System.err.println("  found the best neighbor to go to frontied : " + best.id + " with min= " + min);
          }
          movePod(size, cell, best);
        } else {
          // set with no frontier
          movePod(size, cell, cell);
        }
      }

      if (size > 0) {
        if (Player.DEBUG_CALCULATION) {
          System.err.println("All actions evaluated, still some pods");
        }
      }
    }
  }

  private void movePod(int size, Cell from, Cell target) {
    moveOrders += " " + size + " " + from.id + " " + target.id;
  }

  public void output() {
    System.out.println(moveOrders);
    System.out.println(dropOrders);
  }

}
