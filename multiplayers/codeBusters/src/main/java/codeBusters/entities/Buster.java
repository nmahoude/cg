package codeBusters.entities;

import codeBusters.entities.som.StateOfMind;

public class Buster extends Entity{
  public int team;
  public int state;
  public int value;
  public StateOfMind stateOfMind;
  public Ghost carried;
  public int stun = 0;
  public int stunned = 0;
}
