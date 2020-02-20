package pr;

import java.util.ArrayList;
import java.util.Arrays;
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
  private Cell cells[];
  int distances[][];
  
  Continent japan = new Continent(this);
  Continent antartica = new Continent(this);
  
  
  public List<Cell> world = new ArrayList<>();
  public List<Cluster> clusters = new ArrayList<>();
  
  private int cellCount;
  List<Continent> continents = new ArrayList<>();
  
  public Grid(int cellCount) {
    continents.add(japan);
    continents.add(antartica);
    
    this.cellCount = cellCount;
    initCells = new Cell[cellCount];
    cells = new Cell[cellCount];
    distances = new int [cellCount][cellCount];
    
    for (int i = 0; i < cells.length; i++) {
      initCells[i] = new Cell(i);
      cells[i] = initCells[i];
      
      if (Arrays.asList(149,150,143).contains(i)) {
        japan.addCell(cells[i]);
      }
      if (Arrays.asList(57,67,78,89,97,104,113).contains(i)) {
        antartica.addCell(cells[i]);
      }
    }
  }
  
  public Cell getById(int id) {
    return initCells[id];
  }

  private Continent createContinent() {
    Continent continent = new Continent(this);
    continents.add(continent);
    return continent;
  }

  public void build() {
    buildWorld(); // cells ordered by platinum
    buildDistances();
    
    for (Cell cell : cells) {
      if (cell.continent != null) continue;

      Continent continent = createContinent();
      cell.spread(continent);
    }
    System.err.println("Counting " + continents.size() +" continents");
    for (Continent c : continents) {
      c.closeContinent();
    }
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

  public void updateContinents() {
    for (Continent continent : continents) {
      continent.update();
    }
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
}
