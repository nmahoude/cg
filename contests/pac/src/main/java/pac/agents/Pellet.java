package pac.agents;

import pac.map.Pos;

public class Pellet {

  public final Pos pos;
  public double value;
  public int visited;
  
  public Pellet(Pos pos, double value) {
    this.pos = pos;
    this.value = value;
  }
  
  @Override
  public int hashCode() {
    return pos.offset;
  }
  
  @Override
  public boolean equals(Object obj) {
    return this.pos == ((Pellet)obj).pos;
  }
}
