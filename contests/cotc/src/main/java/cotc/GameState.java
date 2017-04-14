package cotc;

import java.util.List;

import cotc.entities.Barrel;
import cotc.entities.CannonBall;
import cotc.entities.Mine;
import cotc.entities.Ship;

public class GameState {
  public int shipCount;

  private List<CannonBall> cannonballs;
  private List<Mine> mines;
  private List<Barrel> barrels;
  private List<Team> teams;
  private List<Ship> ships;

  public void backup() {
  }

  public void restore() {
  }
}
