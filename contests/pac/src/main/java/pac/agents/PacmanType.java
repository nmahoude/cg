package pac.agents;

public enum PacmanType {
  ROCK,
  PAPER,
  SCISSORS,
  DEAD;
  
  
  private PacmanType() {
  }
  
  public static PacmanType fromString(String type) {
    switch(type) {
    case "ROCK": return ROCK;
    case "PAPER": return PAPER;
    case "SCISSORS": return SCISSORS;
    case "DEAD" : return DEAD;
    default: 
      throw new RuntimeException("UNKNOWN PacmanType");
    }
  }

  public boolean canEat(PacmanType otherType) {
    switch (this) {
      case PAPER: return otherType == ROCK;
      case ROCK: return otherType == SCISSORS;
      case SCISSORS: return otherType == PAPER;
    }
    return false;
  }
}
