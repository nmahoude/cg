package botg.ai;

public class StrategyFactory {

  public static Strategy forHero(String heroType) {
    if ("IRONMAN".equals(heroType)) {
      return new IronmanStrategy();
    } else if ("DOCTOR_STRANGE".equals(heroType)) {
      return new StrangeStrategy();
    }
    return null;
  }

}
