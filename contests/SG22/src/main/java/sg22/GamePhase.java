package sg22;

public enum GamePhase {
  MOVE(0),
  GIVE(1),
  THROW(2),
  PLAY(3),
  RELEASE(4),
  DAILY_GET(5),
  END(6);

  public final int index;
  
  GamePhase(int index) {
    this.index = index;
  }
  
  public static GamePhase from(char[] asString) {
    if (asString[0] == 'M') return MOVE;
    if (asString[0] == 'G' ) return GIVE;
    if (asString[0] == 'T') return THROW;
    if (asString[0] == 'P') return PLAY;
    if (asString[0] == 'R') return RELEASE;
    throw new RuntimeException("Unknwon phase : "+String.valueOf(asString));
  }
}
