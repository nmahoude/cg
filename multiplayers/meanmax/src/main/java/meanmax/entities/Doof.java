package meanmax.entities;

import meanmax.Game;

public class Doof extends Entity {
  public static int MAX_RAGE = 300;

  public static double DOOF_MASS = 1.0;
  public static double DOOF_FRICTION = 0.25;
  public static double DOOF_RAGE_COEF = 1.0 / 100.0;
  public static int DOOF_SKILL_DURATION = 3;
  public static int DOOF_SKILL_COST = 30;
  public static int DOOF_SKILL_ORDER = 1;
  public static double DOOF_SKILL_RANGE = 2000.0;
  public static double DOOF_SKILL_RADIUS = 1000.0;
  
  public Doof() {
    super(Game.DOOF, 1, 0.25, true);
    dead = false;
  }

  public int sing() {
    return (int) Math.floor(speed.length() * DOOF_RAGE_COEF);
  }

}
