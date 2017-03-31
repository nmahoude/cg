package gitc.situations;

public class LB {

  int srcId;
  int dstId;
  int distance;

  public LB f(int srcId) {
    this.srcId = srcId;
    return this;
  }

  public LB t(int dstId) {
    this.dstId = dstId;
    return this;
  }

  public LB d(int distance) {
    this.distance = distance;
    return this;
  }

  public LB b() {
    return this;
  }

}
