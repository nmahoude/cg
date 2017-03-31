package gitc.situations;

/** bomb */
public class BB {

  int id;
  int player;
  int srcId;
  int dstId;
  int remainingTurns;

  public BB id(int id) {
    this.id = id;
    return this;
  }

  public BB player(int player) {
    this.player = player;
    return this;
  }

  public BB from(int fromId) {
    this.srcId = fromId;
    return this;
  }

  public BB to(int destId) {
    this.dstId = destId;
    return this;
  }

  public BB turnsLeft(int turns) {
    this.remainingTurns = turns;
    return this;
  }
  public BB build() {
    return this;
  }
  
}
