package gitc.situations;

public class FB {
  int id;
  int player;
  int prod;
  int units;
  int disabled;
  
  public FB id(int id) {
    this.id = id;
    return this;
  }

  public FB player(int playerId) {
    player = playerId;
    return this;
  }
  public FB mine() {
    player = 1;
    return this;
  }
  public FB opp() {
    player = -1;
    return this;
  }
  public FB neutral() {
    player = 0;
    return this;
  }

  public FB prod(int prod) {
    this.prod = prod;
    return this;
  }

  public FB units(int units) {
    this.units = units;
    return this;
  }
  
  public FB disabled(int turns) {
    this.disabled = turns;
    return this;
  }
  
  public FB build() {
    return this;
  }

  
}
