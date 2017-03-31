package gitc.entities;

import java.util.Scanner;

import gitc.GameState;

public class Bomb extends MovableEntity {
  private static final int DAMAGE_DURATION = 5;

  public Bomb(int id) {
    super(0, null);
  }
  
  public Bomb(Owner owner, Factory src, Factory dst, int turnsToTarget) {
    super(0, owner);
    source = src;
    destination = dst;
    this.remainingTurns = turnsToTarget;
  }
  
  @Override
  public String toString() {
    return "BOMB p("+playerId+") from "+source.id+" to "+destination.id+" t("+remainingTurns+")";
  }
  
  @SuppressWarnings("unused")
  public void read(Scanner in) {
    readPlayer(in.nextInt());
    source = GameState.factories[in.nextInt()];
    int dstIndex = in.nextInt();
    if (dstIndex != -1) {
      destination = GameState.factories[dstIndex];
      destination.bombIncomming = true;

      remainingTurns = in.nextInt();
    } else {
      int unusedRemainingTurns = in.nextInt();
      if (remainingTurns > 0) {
        remainingTurns--;
      }
    }
    int unused = in.nextInt();
  }

  public void explode() {
    if (destination != null) {
      int damage = Math.min(destination.units, Math.max(10, destination.units / 2));
      destination.units -= damage;
      destination.disabled = DAMAGE_DURATION;
    }
  }
  
}