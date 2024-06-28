package ooc.ai.search.scorers;

import ooc.Oracle;
import ooc.State;
import ooc.ai.search.Node;

public abstract class Scorer {

  public abstract void reset();
  public abstract double calculate(State initState, Node node, Oracle oracle);
  
  public String debug() { return ""; }


  
  
}
