package cotc.ai.ag.features;

public class ShipWeight {
  public static final ShipWeight FULL_ATTACK = new ShipWeight("F_A"); 
  static {
    // force to go closer to ennemies
    FULL_ATTACK.weights[ShipFeature.MY_HEALTH_FEATURE] = 1.0;
    FULL_ATTACK.weights[ShipFeature.DISTANCE_TO_ALL_ENEMY_FEATURE] = -0.1;
    FULL_ATTACK.weights[ShipFeature.DISTANCE_TO_CLOSEST_ENEMY_FEATURE] = -1.0;
  }

  public static final ShipWeight RANGE_ATTACK = new ShipWeight("R_A");
  static {
    // stay away from close range (needed for ship with some health still)
    RANGE_ATTACK.weights[ShipFeature.MY_HEALTH_FEATURE] = 1.0;
    RANGE_ATTACK.weights[ShipFeature.DISTANCE_TO_ALL_ENEMY_FEATURE] = 0.1;
    RANGE_ATTACK.weights[ShipFeature.DISTANCE_TO_CLOSEST_ENEMY_FEATURE] = 0.1;
  }

  
  public static final ShipWeight FULL_RETREAT= new ShipWeight("F_R"); 
  static {
    // force to go farther to ennemies, but stay close to center (hope so)
    FULL_RETREAT.weights[ShipFeature.SPEED_FEATURE] = 10.0;
    FULL_RETREAT.weights[ShipFeature.MY_HEALTH_FEATURE] = 10.0;
    FULL_RETREAT.weights[ShipFeature.DISTANCE_TO_ALL_ENEMY_FEATURE] = 1.0;
    FULL_RETREAT.weights[ShipFeature.DISTANCE_TO_CLOSEST_ENEMY_FEATURE] = 1.0;
    FULL_RETREAT.weights[ShipFeature.DISTANCE_TO_CENTER_FEATURE] = -1.0;
  }

  public static final ShipWeight LESS_THAN_25_HEALTH = new ShipWeight("<25");
  static {
    LESS_THAN_25_HEALTH.weights[ShipFeature.MY_HEALTH_FEATURE] = 1.0;
    LESS_THAN_25_HEALTH.weights[ShipFeature.SPEED_FEATURE] = 1.0;
    LESS_THAN_25_HEALTH.weights[ShipFeature.DISTANCE_TO_CLOSEST_BARREL_FEATURE] = -1.0;
  }

  public static final ShipWeight STANDARD = new ShipWeight("S");
  static {
    STANDARD.weights[ShipFeature.MY_HEALTH_FEATURE] = 1.0;
    STANDARD.weights[ShipFeature.SPEED_FEATURE] = 1.0;
    STANDARD.weights[ShipFeature.DISTANCE_TO_CLOSEST_BARREL_FEATURE] = -0.2;
  }

  public double weights[] = new double[ShipFeature.LAST];
  private String name;
  
  private ShipWeight(String name) {
    this.name = name;
    soundValues();
  }

  private void soundValues() {
    weights[ShipFeature.MY_HEALTH_FEATURE] = 1.0;
    weights[ShipFeature.SPEED_FEATURE] = 1.0;
    weights[ShipFeature.DISTANCE_TO_CLOSEST_BARREL_FEATURE] = -0.2;
  }

  public String output() {
    return name;
  }
}
