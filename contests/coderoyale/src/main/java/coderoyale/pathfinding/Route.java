package coderoyale.pathfinding;

import java.util.ArrayList;
import java.util.List;

import coderoyale.CommandException;
import trigonometry.Point;

public class Route {
  
  public Route(Segment segment) {
    currentEnd = segment;
    from = segment.from;
    target = segment.to;
    updateLengths();
  }
  public Route(Route parent, Segment child) {
    from = parent.from;
    target = parent.target;
    
    for (Segment s : parent.segmentsSoFar) {
      segmentsSoFar.add(s);
    }
    segmentsSoFar.add(child);
    currentEnd = new Segment(child.to, target);
    updateLengths();
  }
  
  
  Point from;
  Point target;
  
  double currentLength;
  double remainingLength;
  
  List<Segment> segmentsSoFar = new ArrayList<>();
  Segment currentEnd;
  
  private void updateLengths() {
    currentLength = 0;
    for (Segment s : segmentsSoFar) {
      currentLength += s.length;
    }
    remainingLength = currentEnd.length;
  }
  public double length() {
    return currentLength + remainingLength;
  }
  public void go() {
    Point to = segmentsSoFar.get(0).to;
    throw CommandException.success("MOVE " +  (int)(to.x)+" "+ (int)(to.y));    
  }
}
