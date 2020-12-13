package dab;

import java.util.ArrayList;
import java.util.List;

public class State {
  private final int boardSize;
  private final int edgeSize;
  final Box[] boxes;
  List<Edge> allEdges = new ArrayList<>();
  
  public State(int boardSize) {
    this.boardSize = boardSize;
    boxes = new Box[boardSize*boardSize];
    
    for (int y=0;y<boardSize;y++) {
      for (int x=0;x<boardSize;x++) {
        String name= ""+(char)('A'+x)+(char)('1'+y);
        boxes[x + boardSize * y] =new Box(name);
      }
    }
    
    edgeSize = boardSize+1;
    for (int y=0;y<edgeSize;y++) {
      for (int x=0;x<edgeSize;x++) {
        if (x < boardSize) {
          Edge hEdge = new Edge("H");
          allEdges.add(hEdge);
          if (y > 0) box(x,y-1).setEdges(Edge.TOP, hEdge); 
          if (y<boardSize) box(x, y).setEdges(Edge.BOTTOM, hEdge);
        }
        

        if (y < boardSize) {
          Edge vEdge = new Edge("V");
          allEdges.add(vEdge);
          if (x<boardSize) box(x,y).setEdges(Edge.LEFT, vEdge); 
          if (x>0) box(x-1, y).setEdges(Edge.RIGHT, vEdge);
        }
        
      }
    }
    
  }

  public Box box(int x, int y) {
    return boxes[x + boardSize * y];
  }

  public void fillAll() {
    for (Edge e: allEdges) {
      e.set();
    }
    for (Box b : boxes) {
      b.setEdges = 4;
    }
  }
}
