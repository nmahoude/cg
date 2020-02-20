package meanmax.entities;

import meanmax.Game;
import meanmax.Player;
import trigo.Position;

public class Tanker extends Entity {
  public static int TANKER_THRUST = 500;
  public static double TANKER_EMPTY_MASS = 2.5;
  public static double TANKER_MASS_BY_WATER = 0.5;
  public static double TANKER_FRICTION = 0.40;
  public static double TANKER_RADIUS_BASE = 400.0;
  public static double TANKER_RADIUS_BY_SIZE = 50.0;
  public static int TANKER_EMPTY_WATER = 1;
  public static int TANKER_MIN_SIZE = 4;
  public static int TANKER_MAX_SIZE = 10;
  public static double TANKER_MIN_RADIUS = TANKER_RADIUS_BASE + TANKER_RADIUS_BY_SIZE * TANKER_MIN_SIZE;
  public static double TANKER_MAX_RADIUS = TANKER_RADIUS_BASE + TANKER_RADIUS_BY_SIZE * TANKER_MAX_SIZE;
  public static double TANKER_SPAWN_RADIUS = 8000.0;
  public static int TANKER_START_THRUST = 2000;

  
  public int water;
  public int size;

  private int b_water;
  private int b_size;

  public void backup() {
    super.backup();
    b_water = water;
    b_size = size;
  }
  public void restore() { 
    super.restore();
    water = b_water;
    size = b_size;
  }
  public Tanker() {
    super(Game.TANKER, 2.5, 0.4, true);
  }

  public Tanker(int size, Player player) {
    this();
    this.size = size;
    //this.player = player;
  }
  public boolean isFull() {
    return water >= size;
  }
  
  public void play() {
    if (isFull()) {
      // Try to leave the map
      thrust(Game.WATERTOWN, -TANKER_THRUST);
    } else if (distance2(Game.WATERTOWN) > Game.WATERTOWN_RADIUS_2) {
      // Try to reach watertown
      thrust(Game.WATERTOWN, TANKER_THRUST);
    }
  }

  public void die() {
    dead = true;

    // Don't spawn a wreck if our center is outside of the map
    if (distance2(Game.WATERTOWN) >= Game.MAP_RADIUS_2) {
      return;
    }

    // create a wreck
    Wreck wreck = Game.wrecks[Game.wrecks_FE++];
    wreck.unitId = 99;
    wreck.dead = false;
    Game.entities[Game.entities_FE++] = wreck;
    wreck.update(round(position.x), round(position.y), water, radius);
  }

  public double distance2(Position pos) {
    return this.position.dist2(pos);
  }

  public void spawnTanker() {
    // NOTE can't know the spawn next spot, or can we ?
  }

  void readExtra(int extra, int extra2) {
    water = extra;
    size = extra2;
    
    mass = TANKER_EMPTY_MASS + TANKER_MASS_BY_WATER * water;
    radius = TANKER_RADIUS_BASE + TANKER_RADIUS_BY_SIZE * size;
  }
}
