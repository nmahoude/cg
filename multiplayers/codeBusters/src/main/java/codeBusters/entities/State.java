package codeBusters.entities;

public enum State {
  UNKNOWN(false),
  FREE(true),
  BUSTED(true),
  BASE(false), 
  IN_FOG(true);
  
  private boolean isOnMap;
  State(boolean isOnMap) {
    this.isOnMap = isOnMap;
    
  }
  public boolean isOnMap() {
    return isOnMap;
  }
}
