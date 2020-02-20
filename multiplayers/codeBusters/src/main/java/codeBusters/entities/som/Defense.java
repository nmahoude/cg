package codeBusters.entities.som;

import codeBusters.entities.Buster;

public class Defense extends StateOfMind{

  private Buster toDefend;

  public Defense(Buster self, Buster toDefend) {
    super(self);
    this.toDefend = toDefend;
  }

  @Override
  public String output() {
    return "MOVE "+toDefend.position+" DEFENSE";
  }

}
