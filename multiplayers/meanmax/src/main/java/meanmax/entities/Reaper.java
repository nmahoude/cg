package meanmax.entities;

import meanmax.Game;

public class Reaper extends Entity {

  public Reaper() {
    super(Game.REAPER, 0.5, 0.2, true);
    dead = false;
  }

}
