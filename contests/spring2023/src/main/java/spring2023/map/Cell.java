package spring2023.map;

public class Cell {
  
  public final MapData data;
  public final int index;

  public int resources;
  public int myAnts;
  public int vAnts; // my virtual ants
  public int oppAnts;
  public int beacon;
  public int dedicatedBase;

  public Cell(MapData data, int index) {
      this.data = data;
      this.index =  index;
  }  
  
  @Override
  public String toString() {
    return "["+index+"]";
  }

  public void copyFrom(Cell model) {
    this.myAnts = model.myAnts;
    this.vAnts = model.vAnts;
    this.oppAnts = model.oppAnts;
    this.beacon = model.beacon;
    this.dedicatedBase = model.dedicatedBase;
    
    this.copyResources(model);
  }

  public void copyResources(Cell model) {
    this.resources = model.resources;
  }

}
