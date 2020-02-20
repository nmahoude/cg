package meanmax.entities;

import meanmax.Game;

public class Destroyer extends Entity {

  public Destroyer() {
    super(Game.DESTROYER, 1.5, 0.3, true);
    dead = false;
  }

}
