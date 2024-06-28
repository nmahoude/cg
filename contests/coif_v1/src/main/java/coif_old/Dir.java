package coif_old;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Dir {
  UP(0, -1),
  DOWN(0, 1),
  LEFT(-1, 0),
  RIGHT(1,0);
  
  private static List<Dir> dirs = Stream.of(UP, DOWN, LEFT, RIGHT).collect(Collectors.toList());
  public final int dx;
  public final int dy;

  Dir(int dx, int dy) {
    this.dx = dx;
    this.dy = dy;
    
  }

  public static List<Dir> randomValues() {
    Collections.shuffle(dirs);
    return dirs;
  }
}
