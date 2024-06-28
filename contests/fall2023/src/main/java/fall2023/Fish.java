package fall2023;

import fast.read.FastReader;

public class Fish {
  public static final int UGLY = 3;
  public static final int UGLY_AGGRESSIVE_SPEED = 540;
  public static final int UGLY_NORMAL_SPEED = 270;
  
  public static final int FISH_SWIM_SPEED = 200;
  public static final int FISH_HEARING_RANGE = 1400;
  public static final int FISH_HEARING_RANGE2 = 1400 * 1400;
  public static final int FISH_FLEE_SPEED = 400;
  
  public int id;
  public final Pos pos = new Pos(0,0);
  public final Vec speed = new Vec(0,0);
  
  
  public Fish(int id) {
    this.id = id;
  }


  public void copyFrom(Fish model) {
    this.id = model.id; 
    this.pos.set(model.pos);
    this.speed.set(model.speed);
  }
  
  @Override
  public String toString() {
    return "Fish["+id+"] @"+pos+" / "+speed;
  }


  public void outputDebugOptional() {
    System.err.println("^ "+id+" "+pos.x+" "+pos.y+" "+speed.vx+" "+speed.vy);
  }


  public void readDebugOptional(FastReader in) {
    id = in.nextInt();
    pos.x = in.nextInt();
    pos.y = in.nextInt();
    speed.vx = in.nextInt();
    speed.vy = in.nextInt();
  }
  
  @Override
  public boolean equals(Object obj) {
    return id == ((Fish)obj).id;
  }
}
