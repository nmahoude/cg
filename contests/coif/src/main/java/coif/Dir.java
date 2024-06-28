package coif;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Dir {
  UP(0, 0, -1),
  DOWN(1, 0, 1),
  LEFT(2, -1, 0),
  RIGHT(3, 1,0);
  
  private static List<Dir> dirs = Stream.of(UP, DOWN, LEFT, RIGHT).collect(Collectors.toList());
  public final int dx;
  public final int dy;
  public final int decal;

  Dir(int decal, int dx, int dy) {
    this.decal = decal;
    this.dx = dx;
    this.dy = dy;
    
  }

  public static List<Dir> randomValues() {
    Collections.shuffle(dirs);
    return dirs;
  }
}
