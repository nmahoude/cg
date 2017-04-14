package cotc.entities;

public class Ship extends Entity {
  public final int owner;
  public int orientation;
  public int speed;
  public int health;
  public int cannonCooldown;
  public int mineCoolDown;
  
  private int b_orientation;
  private int b_speed;
  private int b_health;
  private int b_cannonCooldown;
  private int b_mineCoolDown;

  public Ship(int entityId, int x, int y, int arg1, int arg2, int arg3, int arg4) {
    super(EntityType.SHIP, entityId, x, y);
    orientation = arg1;
    speed = arg2;
    health = arg3;
    owner = arg4;
  }

  public void backup() {
    super.backup();

    b_orientation = orientation;
    b_speed = speed;
    b_health = health;
    b_cannonCooldown = cannonCooldown;
    b_mineCoolDown = mineCoolDown;
  }
  public void restor() {
    super.restore();

    orientation = b_orientation;
    speed = b_speed;
    health = b_health;
    cannonCooldown = b_cannonCooldown;
    mineCoolDown = b_mineCoolDown;
  }

  public void update(int x, int y, int arg1, int arg2, int arg3, int arg4) {
    super.update(x, y);
    orientation = arg1;
    speed = arg2;
    health = arg3;
  }
  
}
