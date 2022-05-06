package spring2022;


import fast.read.FastReader;

public class Unit {

  public int id;
  public final Pos pos = new Pos();
  public final Vec speed = new Vec();
  public int shieldLife;
  public int isControlled;
  public int health;
  public boolean nearBase;
  public int threatFor;

  
  // for Simulator calculations
  public double dx, dy;
  public boolean isPushed;

  public int controlTargetCount;
  public final Pos controlTarget = new Pos();
  public int controlNextTargetCount;
  public final Pos controlNextTarget = new Pos();
  
  @Override
  public String toString() {
    return "u:"+id+" @"+pos;
  }
  
  public void read(FastReader in) {
    pos.x = in.nextInt();
    pos.y = in.nextInt();
    shieldLife = in.nextInt();
    isControlled = in.nextInt();
    health = in.nextInt();
    speed.vx = in.nextInt();
    speed.vy = in.nextInt();
    nearBase = in.nextInt() == 1;
    threatFor = in.nextInt();

    dx = dy = 0;

    if (Player.inversed) {
      pos.inverse();
      speed.inverse();
    }
    
    debugInput();
  }

  public void update(State state) {
    pos.add(speed);
    
    if (!nearBase) {
      int dist2ToMyBase = pos.dist2(State.myBase);
      int dist2ToOppBase = pos.dist2(State.oppBase);

      if (dist2ToMyBase < State.BASE_TARGET_KILL2) {
        state.health[0]--;
        this.health = 0;
      } else if (dist2ToOppBase < State.BASE_TARGET_KILL2) {
        state.health[1]--;
        this.health = 0;
      } else if (dist2ToMyBase < State.BASE_TARGET_DIST2) {
        speed.target(pos, State.myBase);
      } else if (dist2ToOppBase < State.BASE_TARGET_DIST2) {
        speed.target(pos, State.oppBase);
      }
    }
  }

  public void copyFrom(Unit model) {
    id = model.id;
    pos.x = model.pos.x;
    pos.y = model.pos.y;
    
    dx = dy = 0;
    
    shieldLife = model.shieldLife;
    isControlled = model.isControlled;
    health = model.health;
    speed.copyFrom(model.speed);
    nearBase = model.nearBase;
    threatFor  = model.threatFor;
  }

  public boolean isDead() {
    return health <= 0;
  }

  public final boolean isInRange(Pos pos, int range) {
    return this.pos.dist2(pos) <= range*range;
  }

  public boolean isInRange(Unit unit, int range) {
    return this.pos.dist2(unit.pos) < range*range;
  }

  public boolean isInFog() {
    return shieldLife == -1000;
  }

  public boolean hasShield() {
    return shieldLife > 0;
  }

  public boolean isInHitRange(Hero hero) {
    return this.pos.isInRange(hero.pos, State.MONSTER_TARGET_KILL);
  }

  public boolean isInRangeOfMyBase() {
    return this.isInRange(State.myBase, State.BASE_TARGET_DIST);
  }
  public boolean isInRangeOfOppBase() {
    return this.isInRange(State.oppBase, State.BASE_TARGET_DIST);
  }

  public void debugInput() {
    int dpx = pos.x;
    int dpy = pos.y;
    int dvx = (int)speed.vx;
    int dvy = (int)speed.vy;
    if (Player.inversed) {
      dpx = State.WIDTH-pos.x;
      dpy = State.HEIGHT-pos.y;
      dvx = -dvx;
      dvy = -dvy;
    }
    
    if (State.DEBUG_INPUTS) System.err.println("^"+id +" 0 " + dpx+" "+dpy+" "+shieldLife+" "+isControlled+" "+health+" "+dvx+" "+dvy+" "+( nearBase ? "1" : "0" )+" "+threatFor);
  }

  public boolean isInRange(Hero hero, int range) {
    return isInRange(hero.pos, range);
  }

  public boolean isInViewRange(Hero hero) {
    return isInRange(hero.pos, State.HERO_VIEW_RADIUS);
  }

  public boolean forbiddenToHit() {
    return health <= 0 ||  isInRange(State.oppBase, State.BASE_TARGET_DIST);
  }

  
}
