package _fx.modules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cgfx.Board;
import cgfx.Inset;
import cgfx.Length;
import cgfx.Pos;
import cgfx.components.GameOptionPane;
import cgfx.components.GameViewer;
import javafx.scene.paint.Color;
import spring2023.Action;
import spring2023.State;
import spring2023.map.Cell;
import spring2023.map.Map;
import spring2023.map.MapData;

public class S23GameViewer extends GameViewer {
  private static final Color CRISTAL = Color.DARKGOLDENROD;

  private static final Color EGG = Color.DARKCYAN;

  private S23GameWrapper wrapper;
  private S23GameOptionPane options = new S23GameOptionPane();

  Board board = new Board(this, 1100, 700);

  private State state;
  
  
  public S23GameViewer(S23GameWrapper wrapper) {
    this.wrapper = wrapper;
    this.state = wrapper.state;
    
    calculatePos();
  }

  @Override
  protected void updateView() {
    
    drawBoard();
    drawReachables();
    drawBases();
    drawIndexes();
    drawResources();
    drawAnts();
    
    drawBeacons();
  }

  


  private void drawBeacons() {
    for (Cell cell : state.cells) {
      if (wrapper.beacons[cell.index] > 0) {
        board.drawRect(Color.BLUE, Pos.from(cell.data.x, cell.data.y), Length.of(64, 64));
        board.drawRect(Color.BLUE, Color.BLUE, Pos.from(cell.data.x, cell.data.y), Length.of(64, 64), Inset.of(1));
        board.drawRect(Color.BLUE, Color.BLUE, Pos.from(cell.data.x, cell.data.y), Length.of(64, 64), Inset.of(2));
        
        board.drawText(Color.WHITE, Pos.from(cell.data.x, cell.data.y).add(50, 12), ""+wrapper.beacons[cell.index]);
      }
    }
  }

  private void drawReachables() {
    if (!options.isSelected(S23GameOptionPane.DRAW_REACHABLE)) return;
    
    int[] distances = new int[Map.cells.length];
    for (Cell cell : state.cells) {
      distances[cell.index] = Integer.MAX_VALUE;
    }
    
    
    List<MapData> toVisit = new ArrayList<>();
    List<MapData> visited = new ArrayList<>();

    for (int i=0;i<State.numberOfBases;i++) {
      MapData base = Map.myBases[i];
      toVisit.add(base);
      distances[base.index] = 0;
    }
    
    int length = 0;

    while (!toVisit.isEmpty()) {
      Set<MapData> nextLayerToVisit = new HashSet<>();

      length++;
      while (!toVisit.isEmpty()) {
        MapData current = toVisit.remove(0);
        if (state.cells[current.index].myAnts == 0) continue;

        visited.add(current);

        
        for (MapData neighbor : current.neighbors) {
          if (toVisit.contains(neighbor)) continue;
          if (visited.contains(neighbor)) continue;

          nextLayerToVisit.add(neighbor);
          distances[neighbor.index] = length;
        }
      }
      toVisit.clear();
      toVisit.addAll(nextLayerToVisit);
    }
    
    for (MapData cell : visited) {
      Pos pos = Pos.from(cell.x ,  cell.y);
      Color color = Color.LIGHTBLUE;
      board.fillRect(Color.BLACK, color, pos, Length.of(64, 64), Inset.of(1));
    }
    
  }




  static int[][] deltas = new int[][] { 
    {64, 0}, // 0
    {32, -64}, // 1
    {-32, -64}, // 2
    {-64, 0}, // 3
    {-32, 64}, // 4
    {32, 64}, // 5
};



  private void calculatePos() {
    List<MapData> toVisit = new ArrayList<>();
    Set<MapData> visited = new HashSet<>();
    
    toVisit.add(Map.cells[0]);
    Map.cells[0].x = 500;
    Map.cells[0].y = 300;
    
    while (!toVisit.isEmpty()) {
      MapData current = toVisit.remove(0);
      visited.add(current);
      
      for (int n=0;n<6;n++) {
        MapData c = current.neighborsArray[n];
        if (c == MapData.VOID) continue;
        if (toVisit.contains(c)) continue;
        if (visited.contains(c)) continue;
        
        toVisit.add(c);
        c.x = current.x + deltas[n][0];
        c.y = current.y + deltas[n][1];
      }
    }
  
  }

  private void drawBases() {
    for( Cell current : state.cells) {
      Pos pos = Pos.from(current.data.x ,  current.data.y);
      if (Map.cells[current.index].isMyBase) {
        board.fillRect(Color.BLACK, Color.BLUE, pos, Length.of(64, 64), Inset.of(16));
      } else if (Map.cells[current.index].isOppBase) {
        board.fillRect(Color.BLACK, Color.RED, pos, Length.of(64, 64), Inset.of(16));
      }
    }
  }
  
  private void drawBoard() {
    board.clear();
    
    
    for( Cell current : state.cells) {
      Pos pos = Pos.from(current.data.x ,  current.data.y);
      Color color = Color.GREY;
      board.fillRect(Color.BLACK, color, pos, Length.of(64, 64), Inset.of(1));
    }
    
    board.drawText(Color.BLACK, Pos.from(10,20), "Scores : "+state.myScore+" / "+state.oppScore);
    
  }

  private void drawIndexes() {
    for( Cell current : state.cells) {
      Pos pos = Pos.from(current.data.x ,  current.data.y);
      if (options.isSelected(S23GameOptionPane.DRAW_CELL_INDEX)) {
        board.drawText(Color.BLACK, pos.add(44, 56), ""+current.index);
      }
    }
  }

  private void drawResources() {
    for( Cell current : state.cells) {
      if (current.resources == 0) continue;
      
      Pos pos = Pos.from(current.data.x,  current.data.y);
      Color color;
      if (current.data.type == Map.CELL_EGGS) {
        color = EGG;
      } else {
        color = CRISTAL;
      }
      
      //board.fillRect(color, color, pos.add(20, 20), Length.of(24, 24), Inset.of(0));
      board.fillCircle(color, pos.add(32, 32), 16);
      board.drawText(Color.BLACK, pos.add(20, 20).add(4, 20), ""+current.resources);
    }
  }

  
  private void drawAnts() {
    for( Cell current : state.cells) {
      if (current.myAnts > 0) {
        Pos pos = Pos.from(current.data.x,  current.data.y);
        Color color = Color.BLUE;
        board.fillRect(color, color, pos, Length.of(32,16));
        board.drawText(Color.WHITE, pos.add(2, 14), ""+current.myAnts);
      }
      
      if (options.isSelected(S23GameOptionPane.DRAW_OPP_ANTS) && current.oppAnts > 0) {
        Pos pos = Pos.from(current.data.x,  current.data.y);
        Color color = Color.RED;
        board.fillRect(color, color, pos.add(0, 48), Length.of(32,16));
        board.drawText(Color.WHITE, pos.add(2, 60), ""+current.oppAnts);
      }
    }
  }
  @Override
  public void setOptionsPane(GameOptionPane options) {
    this.options = (S23GameOptionPane)options;
  }

  public void setActions(List<Action> actions) {
  }
	
}
