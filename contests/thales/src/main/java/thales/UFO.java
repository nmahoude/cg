package thales;

public class UFO extends Entity {
  private static final double DIST_RATIO = 1 / (Player.WIDTH * Player.WIDTH);
  private static final double DISTXY_RATIO = 1 / Math.pow(Player.WIDTH + Player.HEIGHT, 2);

  public final int shield = 0;

  public boolean flag;
  public int boostTimer = 0;
  
  public boolean _flag;
  public int _boostTimer = 0;
  
  public UFO(Team team, Team otherTeam, int id) {
    super(team, otherTeam, id, Entity.UFO, 400);
  }
  
  public void backup() {
    super.backup();
    _flag = flag;
    _boostTimer = boostTimer;
  }
  
  public void restore() {
    super.restore();
    flag = _flag;
    boostTimer = _boostTimer;
  }

  public void update(int x, int y, int vx, int vy, boolean flag) {
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
    this.flag = flag;

    if (Player.DEBUG_OUTPUT) {
      debug();
    }
  }

  public void debug() {
    System.err.println(String.format("%d %d %d %d %d", (int)x, (int)y, (int)vx, (int)vy, flag?1:0));
  }

  
  public void apply(double angle, double thrust) {
    if (thrust > 400) {
      if (boostTimer > 0) {
        thrust = 0;
      } else {
        boostTimer = 8;
      }
    }
    vx += thrust * Math.cos(angle);
    vy += thrust * Math.sin(angle);
  }
  
  public void move(double t) {
    x = x+vx*t;
    y = y+vy*t;
  }
  
  private void tryToRemoveFlag(UFO ufo1, UFO ufo2) {
    double speed1 = ufo1.speed_2();
    double speed2 = ufo2.speed_2();
    if (speed1 > speed2 && ufo2.flag) {
      ufo2.removeFlag();
    }
    if (speed2 > speed1 && ufo1.flag) {
      ufo1.removeFlag();
    }
  }
  
  public void removeFlag() {
    flag = false;
    this.otherTeam.flag.x = this.otherTeam.depX;
    this.otherTeam.flag.y = this.otherTeam.depY;
  }
  
  private double speed_2() {
    return vx*vx+vy*vy;
  }
  public void grabFlag() {
    flag = true;
    otherTeam.flag.grab();    
  }
  
  public void applyTarget(double tx, double ty, double thrust) {
    double dx = tx-x;
    double dy = ty-y;
    double l = Math.sqrt(dx*dx+dy*dy);
    
    vx += thrust * dx / l;
    vy += thrust * dy / l;
  }

  public double distance_2_ToGoal() {
    // TODO sqrt ?
    return (myTeam.depX - x)*(myTeam.depX - x) * DIST_RATIO;
  }

  public double distance_2(Flag flag) {
    return ((flag.x - x)*(flag.x - x) + (flag.y - y)*(flag.y - y));
  }

  public double distance_2(UFO ufo) {
    return ((ufo.x - x)*(ufo.x - x) + (ufo.y - y)*(ufo.y - y));
  }

  public void score() {
    if (!flag) return;
    if (myTeam.depX < 2000 && x <= 1000 ||
        myTeam.depX > 8000 && x >= 9000) {
      myTeam.score++;
      removeFlag();
    }
  }

  public void prepareBoostTimer() {
    boostTimer = 8;
    _boostTimer = 8;
  }

  public void decreaseBoostTimer() {
    if (boostTimer > 0) {
      boostTimer--;
    }
  }
}
