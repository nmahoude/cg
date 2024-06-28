package spring2023.map;

import java.util.ArrayList;
import java.util.List;

public class Path {
  public MapData origin;
  public MapData target;
  public final List<MapData> path = new ArrayList<>();

  public Path() {
    
  }
  
  public Path(MapData source, MapData cell) {
    this.origin = source;
    this.target = cell;
  }
  
  @Override
  public String toString() {
    return ""+origin+"=>"+target+" ("+path.size()+")="+path;
  }

  public int size() {
    return path.size();
  }

  public void clear() {
    this.origin = null;
    this.target = null;
    path.clear();
  }

  public void copyFrom(Path model) {
    this.origin = model.origin;
    this.target = model.target;
    this.path.clear();
    this.path.addAll(model.path);
  }
}
