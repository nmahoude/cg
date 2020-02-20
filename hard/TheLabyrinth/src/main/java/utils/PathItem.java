package utils;


public class PathItem {
  int cumulativeLength = 0;
  int totalPrevisionalLength = 0;
  PathItem precedent = null;
  P pos;

  public P getPosition() {
    return pos;
  }
  public int length() {
    PathItem i = this;
    int count = 0;
    while (i != null) {
      count++;
      i = i.precedent;
    }
    return count;
  }
}