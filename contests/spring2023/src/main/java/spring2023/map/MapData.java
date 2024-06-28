package spring2023.map;

import java.util.ArrayList;
import java.util.List;

public class MapData {
  public static final MapData VOID = new MapData(-1);
  
  public int x=Integer.MIN_VALUE;
  public int y=Integer.MIN_VALUE;

  public final int index;
  public int type;

  public boolean isMyBase;
  public boolean isOppBase;

  public List<MapData> neighbors = new ArrayList<>();
  public MapData[] neighborsArray = new MapData[6];
  
  public MapData(int index) {
    super();
    this.index = index;
  }

  @Override
  public String toString() {
    return "["+index+"]";
  }
}
