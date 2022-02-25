package dab2;

import java.util.ArrayList;
import java.util.List;

public class Box {
  public static final Box WALL = new Box(-1, -1);
  private static Box[] boxes = new Box[7*7];
  static {
    for (int y=0;y<7;y++) {
      for (int x=0;x<7;x++) {
        boxes[x+7*y] = new Box(x,y);
      }
    }
    
    for (int y=0;y<7;y++) {
      for (int x=0;x<7;x++) {
        Box box = box(x,y);
        if (y<6) box.neighbors.add(box(x,y+1));
        if (x<6) box.neighbors.add(box(x+1,y));
        if (y>0) box.neighbors.add(box(x,y-1));
        if (x>0) box.neighbors.add(box(x-1,y));
      }
    }
    
    
    
  }
  public final int x;
  public final int y;
  public List<Box> neighbors = new ArrayList<Box>();
  
  private Box(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  @Override
  public String toString() {
    return String.format("(%d, %d)",x,y );
  }
  
  public static Box box(int x, int y) {
    return boxes[x+7*y];
  }


  public Box adjacent(Dir dir) {
    if (dir == Dir.TOP && y<6) return box(x, y+1);
    if (dir == Dir.BOTTOM && y > 0) return box(x, y-1);
    if (dir == Dir.RIGHT && x < 6) return box(x+1, y);
    if (dir == Dir.LEFT && x > 0) return box(x-1, y);
    
    return WALL;
  }
  
}
