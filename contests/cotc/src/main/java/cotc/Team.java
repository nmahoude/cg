package cotc;

import java.util.ArrayList;
import java.util.List;

import cotc.entities.Ship;

public class Team {
  public final int id;

  public boolean dead;
  public final List<Ship> ships = new ArrayList<>();
  public final List<Ship> shipsAlive = new ArrayList<>();
  
  private boolean b_dead;
  public final List<Ship> b_ships = new ArrayList<>();
  private final List<Ship> b_shipsAlive = new ArrayList<>();

  public Team(int id) {
    this.id = id;
  }

  public void backup() {
    b_dead = dead;

    b_ships.clear();
    b_ships.addAll(ships);
    
    b_shipsAlive.clear();
    b_shipsAlive.addAll(shipsAlive);
  }

  public void restore() {
    dead = b_dead;
    
    ships.clear();
    ships.addAll(b_ships);

    shipsAlive.clear();
    shipsAlive.addAll(b_shipsAlive);
  }

  public void setDead() {
    for (Ship ship : ships) {
      ship.health = 0;
    }
  }

  public int getScore() {
    int score = 0;
    for (Ship ship : ships) {
      score += ship.health;
    }
    return score;
  }

  public List<String> toViewString() {
    List<String> data = new ArrayList<>();

    data.add(String.valueOf(this.id));
    for (Ship ship : ships) {
      data.add(ship.toViewString());
    }

    return data;
  }
}
