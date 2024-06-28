package spring2023.simulation;

import spring2023.map.MapData;

public class Allocation {

  public final MapData source;
  public final MapData target;
  public int antsToSend;

  public Allocation(MapData antFrom, MapData atDist, int antsToSend) {
    this.source = antFrom;
    this.target = atDist;
    this.antsToSend = antsToSend;
  }

  @Override
  public String toString() {
    return "Allocation : "+antsToSend+" ants from "+source+" to "+target;
  }
}
