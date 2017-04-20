package cotc.ai.ag.features;

public class ShipWeight {
  public double weights[] = new double[Feature.LAST];
  
  public ShipWeight() {
    soundValues();
  }

  private void soundValues() {
    weights[ShipFeature.MY_HEALTH_FEATURE] = 1.0;
    weights[ShipFeature.SPEED_FEATURE] = 1.0;
    weights[ShipFeature.DISTANCE_TO_CLOSEST_BARREL_FEATURE] = -0.2;
  }
}
