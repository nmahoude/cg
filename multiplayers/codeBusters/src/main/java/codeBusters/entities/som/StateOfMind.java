package codeBusters.entities.som;

import codeBusters.entities.Buster;

public abstract class StateOfMind {
  public Buster self;
  
  public StateOfMind(Buster self) {
    this.self = self;
  }

  public abstract String output() ;
}
