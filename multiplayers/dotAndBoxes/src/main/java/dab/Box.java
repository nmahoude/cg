package dab;

public class Box {
  final String name;
  Edge[] edges = new Edge[4];
  int setEdges = 0;
  
  
  public Box(String name) {
    this.name = name;
  }

  public void setEdge(int index) {
    if (edges[index].isSet()) return;
    
    edges[index].set();
    setEdges++;
  }

  public void unsetEdge(int index) {
    setEdges--;

    if (!edges[index].isSet()) return;
    
    edges[index].unset();
  }

  public void setEdges(int index, Edge edge) {
    edges[index] = edge;
    edge.boxes[edge.boxesFE++] = this;
  }
}
