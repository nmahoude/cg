package tests;

public class RaceFinished extends Exception {
  public int podId;
  public enum TeamType {
    TEAM_1, TEAM_2
  }
  public TeamType team;
  public RaceFinished(int id) {
    podId = id;
    team = id < 2 ? TeamType.TEAM_1 : TeamType.TEAM_2;
  }
  
  @Override
  public String toString() {
    return "Race finished, team "+team+" has won";
  }
}
