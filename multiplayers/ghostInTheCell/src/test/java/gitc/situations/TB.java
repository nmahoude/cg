package gitc.situations;

public class TB {

  public int id;
  public int  player;
  public int srcId;
  public int dstId;
  public int units;
  public int remainingTime;

  public TB id(int id) {
    this.id = id;
    return this;
  }
  
  
  public TB player(int playerId) {
    this.player = playerId;
    return this;
  }
  
  public TB mine() {
    this.player = 1;
    return this;
  }
  
  public TB opp() {
    this.player = -1;
    return this;
  }

  public TB from(int srcId) {
    this.srcId = srcId;
    return this;
  }
  public TB to(int dstId) {
    this.dstId = dstId;
    return this;
  }
  public TB units(int units) {
    this.units = units;
    return this;
  }
  public TB turnsLeft(int tl) {
    this.remainingTime = tl;
    return this;
  }

  public TB build() {
    return this;
  }
}
