package fall2023;

import java.util.ArrayList;
import java.util.List;

import fast.read.FastReader;

public class Drone {
  public static final int NORMAL_LIGHT_RADIUS = 800;
  public static final int BIG_LIGHT_RADIUS = 2000;

  public static final int NORMAL_LIGHT_RADIUS2 = 800*800;
  public static final int BIG_LIGHT_RADIUS2 = 2000 * 2000;
  public static final int MOVE_SPEED = 600;
  public static final int MOVE_SPEED2 = MOVE_SPEED * MOVE_SPEED;
  
  public final int id;
  public final Pos pos = new Pos(0,0);
  public boolean emergency;
  public int battery = 0;
  
  public List<Blip> blips = new ArrayList<>();
  public final Scan currentScans = new Scan();
  
  public Drone(int id, int droneX, int droneY, boolean emergency, int battery) {
    this.id = id;
    pos.x = droneX;
    pos.y = droneY;
    this.emergency = emergency;
    this.battery = battery;
  }

  public void copyFrom(Drone model) {
    this.pos.x = model.pos.x;
    this.pos.y = model.pos.y;
    this.emergency = model.emergency;
    this.battery = model.battery;
    this.currentScans.copyFrom(model.currentScans);
  }

  
  public void outputDebug() {
    System.err.println("^ "+id+" "+pos.x+" "+pos.y+" "+(emergency? "1" : "0")+" "+battery );
  }

  public void outputDebugOptional() {
    System.err.println("^ "+pos.x+" "+pos.y+" "+(emergency? "1" : "0")+" "+battery +" "+currentScans+" ");
  }
  
  public void readDebugOptional(FastReader in) {
    this.pos.x = in.nextInt();
    this.pos.y = in.nextInt();
    this.emergency = in.nextInt() == 1;
    this.battery = in.nextInt();
    this.currentScans.scaned = in.nextInt();
  }
  
  public Drone(int id) {
    this.id = id;
  }

  public void setScan(int id) {
    currentScans.scan(id);
  }
  public boolean hasScan(int id) {
    return currentScans.contains(id);
  }

  public void update(int droneX, int droneY, boolean emergency, int battery) {
    this.pos.x = droneX;
    this.pos.y = droneY;
    this.emergency = emergency;
    this.battery = battery;
    
    currentScans.clear();
    blips.clear();
  }

  public void clear() {
    blips.clear();
    currentScans.clear();;
  }

  @Override
  public String toString() {
    return "Drone["+id+"] @"+pos+" b:"+battery;
  }

  public Blip blipOf(int l) {
    for (Blip blip : blips) {
      if (blip.id == l) return blip;
    }
    return null;
  }

  public boolean canTurnLight() {
    return battery >= 5;
  }

  public int estimateTurnsToSurface() {
    if (this.emergency) {
      return (int)Math.ceil(1.0 * (this.pos.y -500) / 300);
    } else {
      return (int)Math.ceil(1.0 * (this.pos.y -500) / 600);
    }
  }

}
