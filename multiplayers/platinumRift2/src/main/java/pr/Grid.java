package pr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Grid {
  public static Comparator<? super Cell> platinumDESC = new Comparator<Cell>() {
    @Override
    public int compare(Cell o1, Cell o2) {
      return Integer.compare(o2.platinum, o1.platinum); // more to less
    }
  };

  public static Comparator<? super Cell> attractivnessDESC = new Comparator<Cell>() {
    @Override
    public int compare(Cell o1, Cell o2) {
      return Double.compare(o2.attractivness, o1.attractivness); // more to less
    }
  };
  
  private Cell initCells[];
  public Cell cells[];
  int distances[][];
  
  public List<Cell> world = new ArrayList<>();
  public List<Cluster> clusters = new ArrayList<>();
  
  private int cellCount;
  
  public Grid(int cellCount) {
    
    this.cellCount = cellCount;
    initCells = new Cell[cellCount];
    cells = new Cell[cellCount];
    distances = new int [cellCount][cellCount];
    
    for (int i = 0; i < cells.length; i++) {
      initCells[i] = new Cell(this, i);
      cells[i] = initCells[i];
    }
  }
  
  public Cell getById(int id) {
    return initCells[id];
  }

  public void build() {
    buildWorld(); // cells ordered by platinum
    buildDistances();
  }
  
  private void buildDistances() {
    for (Cell cell :world) {
      List<Cell> currentCircle= new ArrayList<>();
      List<Cell> nextCircle = new ArrayList<>();
      currentCircle.add(cell);
      distances[cell.id][cell.id]= -1; 
      int dist = 1;
      while (!currentCircle.isEmpty()) {
        Cell current = currentCircle.remove(0);
        for (Cell n : current.neighbors()) {
          if (distances[cell.id][n.id]==0) {
            distances[cell.id][n.id]= dist;
            if (!nextCircle.contains(n)) {
              nextCircle.add(n);
            }
          }
        }
        if (currentCircle.isEmpty() && !nextCircle.isEmpty()) {
          dist++;
          List<Cell> tmp = currentCircle;
          currentCircle = nextCircle;
          nextCircle = tmp;
        }
      }
    }
  }

  private void buildWorld() {
    world.clear();
    for (int i=0;i<cellCount;i++) {
      world.add(cells[i]);
    }
    world.sort(platinumDESC);
    System.err.println("world-cell 0 : " + world.get(0).id +" => plat = "+world.get(0).platinum);
  }

  public void buildClusters() {
    clusters.clear();
    world.forEach(c -> c.cluster = null);
    
    world.forEach(c -> {
      if (c.cluster != null) return;
      if (!c.isMine()) return;
      Cluster cluster = new Cluster();
      cluster.cells.add(c);
      c.cluster = cluster;
      c.clusterDFS(cluster);
      clusters.add(cluster);
    });
    System.err.println("Found "+clusters.size()+" clusters");
    for (Cluster c: clusters) {
      long frontierSize = c.cells.stream().filter(cell -> cell.clusterFrontier).count();
      System.err.println("Cluster size is "+c.cells.size() + " frontier is "+frontierSize);
    }
  }

	public int cellCount() {
		return cellCount;
	}
}
