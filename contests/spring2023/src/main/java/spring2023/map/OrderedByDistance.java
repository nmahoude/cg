package spring2023.map;

import java.util.ArrayList;
import java.util.List;

public class OrderedByDistance {
  public final MapData origin;
  public List<MapData> allCellsByDistance = new ArrayList<>();

  public OrderedByDistance(MapData origin) {
    this.origin = origin;
  }

}
