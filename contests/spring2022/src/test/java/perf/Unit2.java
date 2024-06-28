package perf;

public class Unit2 {
  int[] parent;
  int offset;
  
  public Unit2(LightState2 state, int offset) {
    parent = state.v;
    this.offset = offset * LightState2.DECAL;
  }
  
  public int health() {
    return parent[offset + 0];
  }
  
  public void setHealth(int h) {
    parent[offset + 0] = h;
  }
  
  public int posx() { return parent[offset+1]; }
  
  public void  setPosy(int y) { parent[offset+2] = y; }
}
