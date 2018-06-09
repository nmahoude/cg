package thales;

public class Flag extends Entity {

  public Flag(Team myTeam, Team otherTeam) {
    super(myTeam, otherTeam, 44, Entity.FLAG, 150);
  }
  
  public void update(int x, int y) {
    if (x < 0) {
      this.x = -1000;
      this.y = -1000;
    } else {
      this.x = x;
      this.y = y;
    }
    if (Player.DEBUG_OUTPUT) {
      debug();
    }
  }

  public void debug() {
    System.err.println(String.format("%d %d", (int)x, (int)y));
  }

  public void grab() {
    this.x = -1000; // be sure to be far from collision
    this.y = -1000;
  }
  
  public void backup() {
    super.backup();
  }
  public void restore() {
    super.restore();
  }

  public boolean onMap() {
    return x >= 0;
  }
} 
