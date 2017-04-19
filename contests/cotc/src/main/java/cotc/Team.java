package cotc;

import java.util.ArrayList;
import java.util.List;

import cotc.entities.Ship;
import cotc.utils.FastArray;

public class Team {
  public final int id;

  public boolean dead;
  public final FastArray<Ship> ships = new FastArray<>(Ship.class, 3);
  public final FastArray<Ship> shipsAlive = new FastArray<>(Ship.class, 3);
  
  private boolean b_dead;
  public final FastArray<Ship> b_ships = new FastArray<>(Ship.class, 3);
  public final FastArray<Ship> b_shipsAlive = new FastArray<>(Ship.class, 3);

  public Team(int id) {
    this.id = id;
  }

  public void backup() {
    b_dead = dead;

    b_ships.copyFrom(ships);
    b_shipsAlive.copyFrom(shipsAlive);
  }

  public void restore() {
    dead = b_dead;
    
    ships.copyFrom(b_ships);
    shipsAlive.copyFrom(b_shipsAlive);
  }

  public void setDead() {
    for (int s=0;s<ships.FE;s++)
      ships.elements[s].health = 0;
  }

  public int getScore() {
    int score = 0;
    for (int s=0;s<ships.FE;s++) {
      score += ships.elements[s].health;
    }
    return score;
  }
}
