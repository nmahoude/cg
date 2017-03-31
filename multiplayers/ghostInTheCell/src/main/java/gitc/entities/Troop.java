package gitc.entities;

import java.util.Scanner;

import gitc.GameState;

public class Troop extends MovableEntity {
  
  public Troop(int id) {
    super(id, null);
  }
  public Troop(Owner owner, Factory src, Factory dst, int unitsToMove) {
    super(0, owner);
    source = src;
    destination= dst;
    units = unitsToMove;
    remainingTurns = src.getDistanceTo(dst);
  }
  
  public void read(Scanner in) {
    readPlayer(in.nextInt());

    source = GameState.factories[in.nextInt()];
    destination = GameState.factories[in.nextInt()];
    units = in.nextInt();
    remainingTurns = in.nextInt();
  }
  public String tddOutput() {
      return ".t(new TB()."
              +"id("+id+")."
              +"player("+playerId+")."
              +"from("+source.id+")."
              +"to("+destination.id+")."
              +"units("+units+")."
              +"turnsLeft("+remainingTurns+"))";
  }
  public void affectToFactory(Factory[] factories) {
    destination.addTroop(this);
  }
}
