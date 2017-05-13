package c4l.entities;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
  int[] storage, expertise;
  boolean dead, attemptConnection, moved;
  int eta, score, deadAt, index;
  String message, connectionData;
  List<Sample> tray;
  Module from, target;

  public PlayerData(int index) {
    int capacity = MoleculeType.values().length;
    from = Module.START_POS;
    target = Module.START_POS;
    eta = 0;
    storage = new int[capacity];
    expertise = new int[capacity];
    this.index = index;
    score = 0;
    tray = new ArrayList<>(3);
  }

  public void die(int round) {
    if (!dead) {
      dead = true;
      deadAt = round;
      score = -1;
    }
  }

  public void reset() {
    message = null;
    attemptConnection = false;
    moved = false;
    connectionData = null;
  }

  public void setMessage(String message) {
    this.message = message;
    if (message != null && message.length() > 19) {
      this.message = message.substring(0, 17) + "...";
    }
  }

  public boolean isMoving() {
    return eta > 0;
  }
}
