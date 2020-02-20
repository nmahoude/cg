package greatescape.ai;

import java.util.List;

import greatescape.PathItem;
import greatescape.WallOrientation;

public class MinimalPath {
  List<PathItem> path;
  
  // wall info
  int x, y;
  WallOrientation wo;

  public int minimalPath = Integer.MAX_VALUE;
}
