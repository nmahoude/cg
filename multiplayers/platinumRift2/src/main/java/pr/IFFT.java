package pr;

import java.util.ArrayList;
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

      
      int sumEnnemies = cell.countEnemiesAround();
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
    	int size = cell.myPods();
    	
    	if (size == 0) continue; // no troups here
      if (Player.DEBUG_CALCULATION) {
        System.err.println("Calculation Moves from " + cell);
      }

      // Verifier si il faut laisser des troupes
      int  neededTroupToDefend = neededTroupToDefendOurself(cell);
      if (cell == Player.myHQ) {
      	neededTroupToDefend = cell.countEnemiesAround();
      }
      size = Math.max(0, size - neededTroupToDefend); // keep some troup here
      if (Player.DEBUG_CALCULATION) {
      	System.err.println("  Can move "+size+" pods without being endangered");
      }

      if (size > 30) {
      	if (Player.DEBUG_CALCULATION) {
      		System.err.println("  Hulk ("+size+") attacking the enemy HQ !");
      	}
      	Cell next = cell.findRouteTo(Player.enemyHQ);
      	if (next != null) {
	      	movePod(size, cell, next);
	      	size = 0;
      	}
      }
      
      
      // Verifier si on peut aider le HQ
      if (Player.myHQ.countEnemiesAround() > 0) {
      	if (Player.DEBUG_CALCULATION) {
      		System.err.println("  HQ threaten, move all forces there");
      	}
      	Cell next = cell.findRouteTo(Player.myHQ);
        movePod(size, cell, next);
        size = 0;
      } else {
      }
      
      // Si on peut attaquer le HQ enemy, let's go
      if (Player.enemyHQ.distanceTo(cell) == 1) {
      	if (Player.DEBUG_CALCULATION) {
      		System.err.println("  can attack enemy HQ => allin");
      	}
        movePod(size, cell, Player.enemyHQ);
        size = 0;
      }
      
      
      // verifier si on peut aider une voisine en detresse
      List<Cell> neighborsByPlatinum = cell.neighbors().stream().sorted(Grid.platinumDESC).collect(Collectors.toList());
      for (Cell v : neighborsByPlatinum) {
      	if (v.isForbiden()) continue;
      	
        if (v.threathen.isEmpty())
          continue;
        if (Player.DEBUG_CALCULATION) {
          System.err.println("Helping sister "+v+" in distress ");
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
          if (Player.DEBUG_CALCULATION) {
            System.err.println("On prend possession d'une enemie");
          }
        	
          movePod(size - voisine.podCount, cell, voisine);
          size -= voisine.podCount;
        }
        if (size <= 0)
          break;
      }

      
      // Explore New
//      if (cell == Player.myHQ) {
//    		System.err.println("Explorer NEW ");
//      	ExplorerNode root = new Explorer().exploreFrom(cell);
//      	int total = 0;
//      	for (ExplorerNode child : root.childs) {
//      		total += child.podsNeeded;
//      		System.err.println("Pods needed from "+child.cell+" is "+child.podsNeeded);
//      	}
//      	
//      	int totalExplorersDispatched = 0;
//      	for (ExplorerNode child : root.childs) {
//      		int explorers = child.podsNeeded * size / total;
//      		System.err.println(" ==> Moving "+explorers+" from "+cell+" to "+child.cell);
//					movePod(explorers, cell, child.cell);
//					totalExplorersDispatched = explorers;
//      	}      	
//      	size -= totalExplorersDispatched;
//      	
//      }
      
      // Explore new new
      if (size > 0) {
      	// check if we have multiple cells to explore
      	List<Cell> toExplore = new ArrayList<>();
      	for (Cell n : cell.neighbors) {
      		if (n.isForbiden()) continue;
      		if (n.isMine()) continue;
      		toExplore.add(n);
      	}
      	if (toExplore.size() > 1) {
      		System.err.println("From "+cell+" we have a split between "+toExplore);
      		
	      	// cell need to split, so check what is the best % ....
      		int totalPerCell[] = new int[toExplore.size()];
      		int total = 0;
      		for (Cell v : Player.grid.cells) {
	      		if (v.isForbiden()) continue;
	      		if (v.isMine()) continue;
	      		
	      		int bestDist = Integer.MAX_VALUE;
	      		int[] subTotal = new int[toExplore.size()];
	      		for (int i=0;i<toExplore.size();i++) {
	      			Cell toEx = toExplore.get(i);
	      			if (cell.id == 62) {
	      				System.err.println("      dist from "+toEx+" to "+v+" is "+Player.grid.distances[v.id][toEx.id]);
	      			}
	      			int dist = Player.grid.distances[v.id][toEx.id];
	      			if (dist == bestDist) {
	      				subTotal[i]++;
	      			} else if (dist < bestDist) {
	      				for (int j=0;j<i;j++) subTotal[j] = 0; // reinit all
	      				subTotal[i]++;
	      				bestDist = Player.grid.distances[v.id][toEx.id];
	      			}
	      		}
	      		for (int i=0;i<toExplore.size();i++) {
	      			totalPerCell[i]+= subTotal[i];
	      			total+=subTotal[i];
	      		}
	      	}
      		
      		System.err.println("by distance, repartition of unknown cell is  : ");
      		for (int i=0;i<toExplore.size();i++) {
      			System.err.println("  "+toExplore.get(i)+" tot = "+totalPerCell[i]);
      		}
      		System.err.println("For a total of "+total);
      		
      		
      		int totalMoved = 0;
      		for (int i=0;i<toExplore.size();i++) {
      			Cell toEx = toExplore.get(i);
      			int toMove = size * totalPerCell[i] / total;
						movePod(toMove, cell, toEx);
						totalMoved += toMove;
      		}
      		size -= totalMoved;
      	}
      }
      
      
      
      // Explore
      List<Cell> potential = cell.neighbors().stream()
          .sorted(Grid.platinumDESC)
          .filter(n -> n.isEmpty())
          .filter(n -> !n.isMine())
          .filter(n -> !n.isForbiden())
          .collect(Collectors.toList());
      if (Player.DEBUG_CALCULATION) {
        System.err.println("On explore les cells vides aux alentours "+potential.toString()+" avec nos "+size+" pods disponibles");
      }

    	for (Cell neighbor : potential) {
        if (size <= 0) break;
        if (neighbor.isForbiden()) {
        	System.err.println("don't go to empty cell "+neighbor+" with no neighbors (except ourselves) and 0 platinium");
        	continue;
        }
        movePod(1, cell, neighbor);
        size--;
      }

      if (size > 0) {
      	System.err.println("il reste des pods (count="+size+")");
      	if (Player.round < -1) {
          if (Player.DEBUG_CALCULATION) {
            System.err.println(" => on envoye selon l'exploration");
            
          }
      	} else {
	        if (Player.DEBUG_CALCULATION) {
	          System.err.println(" => on envoye vers la frontiere");
	        }
	      	
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
	          	if (f.isForbiden()) continue;
	          	
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
      }
      if (size > 0) {
        if (Player.DEBUG_CALCULATION) {
          System.err.println("All actions evaluated, still some pods");
        }
      }
    }
  }

  private int neededTroupToDefendOurself(Cell cell) {
  	int neededTroupToDefend = 0;
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
        neededTroupToDefend = sumEnnemies;
      }
    }
    return neededTroupToDefend;
	}

	private void movePod(int size, Cell from, Cell target) {
    moveOrders += " " + size + " " + from.id + " " + target.id;
  }

  public void output() {
    System.out.println(moveOrders);
    System.out.println(dropOrders);
  }

}
