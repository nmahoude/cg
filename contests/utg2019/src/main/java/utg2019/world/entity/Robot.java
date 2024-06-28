package utg2019.world.entity;


import trigonometryInt.Point;
import utg2019.Owner;

// plausability always 100, but what can he transports ?
public class Robot {
  public static final int MAX_MOVE = 4;

  public final Owner owner;
  public final int id;

  public int t_ore; // % transporting 1 ore, 100% for me
  public int t_mine; // % transporting 1 bomb, 100% for me
  public int t_radar; // % transporting 1 radar, 100% for me
  
  public Point pos = Point.Invalid; // position
  public boolean isConsideredDangerous = false; // true if we stayed on col 0, updated in TrapAdvisor

  
  public Robot(Owner owner, int id) {
    this.owner = owner;
    this.id = id;
  }
  
  public void move(Point newPos) {
    pos = newPos;
  }

  public boolean hasOre() {
    return t_ore == 100;
  }
  public boolean hasMine() {
    return t_mine == 100;
  }
  public boolean hasRadar() {
    return t_radar == 100;
  }

  public void releaseOre() {
    t_ore = 0;
  }

  public void resetTransport() {
    t_ore = 0;
    t_mine = 0;
    t_radar = 0;
  }
  public void setOre(int perc) {
    t_ore = perc;
  }
  public void setMine(int perc) {
    t_mine = perc;
  }
  public void setRadar(int perc) {
    t_radar = perc;
  }

  public boolean isDead() {
    return pos == Point.Invalid;
  }

  public void copyFrom(Robot model) {
    this.t_ore = model.t_ore;
    this.t_mine = model.t_mine;
    this.t_radar = model.t_radar;

    this.pos = model.pos;
    this.isConsideredDangerous = model.isConsideredDangerous;
  }

  public boolean isOnFirstCol() {
    return pos.x == 0;
  }

  public String getId() {
    return ""+(id + (owner == Owner.ME ? 0:5));
  }
  
}
