package codeBusters.entities;

import codeBusters.P;

public class Entity {
  public int id;
  public P position = new P(0,0);
  public int type;
  
  public int lastSeenTurn;
  
  public Entity() {
  	lastSeenTurn = Integer.MIN_VALUE;
	}
}
