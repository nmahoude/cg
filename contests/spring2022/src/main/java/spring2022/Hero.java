package spring2022;

import fast.read.FastReader;

public class Hero {
  public int id;
  public int owner;
  
  public final Pos pos = new Pos();
  public int shieldLife;
  public boolean isControlled;
  
  // values for simulator
  private boolean inFog;
  public double dx, dy;

  public Hero() {
  }

  @Override
  public String toString() {
    return "H("+id+","+owner+") @" +pos;
  }
  
  
  public void copyFrom(Hero model) {
    this.id = model.id;
    this.owner = model.owner;
    this.pos.copyFrom(model.pos);
    this.shieldLife = model.shieldLife;
    this.inFog = model.inFog;
    this.isControlled = model.isControlled;
  }
  
  public void read(FastReader in) {
    pos.x = in.nextInt();
    pos.y = in.nextInt();
    inFog = false;
    shieldLife = in.nextInt();
    
    isControlled = (in.nextInt() == 1);
    int health = in.nextInt();
    int vx = in.nextInt();
    int vy = in.nextInt();
    int nearBase = in.nextInt();
    int threatFor = in.nextInt();
    
    if (State.DEBUG_INPUTS) System.err.println("^"+id + " " + (owner == 0 ? 1 : 2) + " " + pos.x+" "+pos.y+" "+shieldLife+" "+(isControlled ? 1 : 2)+" "+health+" "+vx+" "+vy+" "+nearBase+" "+threatFor);

    if (Player.inversed) {
      pos.x = State.WIDTH-pos.x;
      pos.y = State.HEIGHT-pos.y;
    }
  }

  public boolean isInRange(Unit unit, int range) {
    return this.pos.dist2(unit.pos) <= range*range ;
  }

  public boolean isInFog() {
    return inFog;
  }

  public void setInFog() {
    this.inFog = true;
    this.pos.copyFrom(Pos.VOID);
  }

  public boolean isInRange(Pos opos, int range) {
    return this.pos.isInRange(opos, range);
  }

  public void debug() {
    if (isInFog()) {
      System.err.println(""+id+" ... in Fog");
    } else {
      System.err.println(""+id+" "+pos.output()+" "+shieldLife);
    }
  }

  public void readEncoded(String string) {
    id = (int)string.charAt(0);
    pos.x = (int)string.charAt(1);
    pos.y = (int)string.charAt(2);
    shieldLife = (int)string.charAt(3);
  }
  
  public void debugEncoded() {
    if (isInFog()) {
      System.err.println(""+id+" ... in Fog");
    } else {
      System.err.println(""+(char)id+""+pos.outputEncoded()+(char)shieldLife);
    }
  }

  public boolean hasShield() {
    return shieldLife > 0;
  }

  public boolean isInRange(Hero hero, int range) {
    return isInRange(hero.pos, range);
  }
}
