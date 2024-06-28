package ooc;

public enum SonarResult {
  NONE,
  PRESENT,
  ABSENT;
  
  public static SonarResult fromString(String result) {
    if ("N".equals(result)) return ABSENT;
    else if ("Y".equals(result)) return PRESENT;
    else return NONE;
  }
}
