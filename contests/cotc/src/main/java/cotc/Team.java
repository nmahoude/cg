package cotc;

import java.util.ArrayList;
import java.util.List;

import cotc.entities.Ship;

public class Team {
  public final List<Ship> ships = new ArrayList<>();
  public final List<Ship> shipsAlive = new ArrayList<>();
  public final int id;

  public int timeout;
  private int b_timeout;

  public Team(int id) {
    this.id = id;
  }

  public void backup() {
    b_timeout = timeout;
  }

  public void restore() {
    timeout = b_timeout;
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
