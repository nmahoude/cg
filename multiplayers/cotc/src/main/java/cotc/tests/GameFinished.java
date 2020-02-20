package cotc.tests;

public class GameFinished extends Exception {
  private static final long serialVersionUID = 2467237233824636092L;

  public int teamId;
  public GameFinished(int id) {
    teamId = id;
  }
  
  @Override
  public String toString() {
    return "Game finished, team "+teamId+" has won";
  }
}
