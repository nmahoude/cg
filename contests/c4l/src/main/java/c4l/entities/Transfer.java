package c4l.entities;

import c4l.Referee;

public abstract class Transfer {
  PlayerData player;

  public Transfer(PlayerData player) {
    this.player = player;
  }

  public abstract void apply(Referee refere);

  public abstract Translatable getSummary();
}
