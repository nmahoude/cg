package calmBronze;

import java.util.Comparator;

import planner.state.Agent;
import planner.state.State;
import util.P;

public class Table {
  public static final Comparator<? super Table> closestToMe = new Comparator<Table>() {
    @Override
    public int compare(Table o1, Table o2) {
      return Integer.compare(Math.abs(o1.pos.x-BronzePlayer.playerX)+Math.abs(o1.pos.y-BronzePlayer.playerY), 
          Math.abs(o2.pos.x-BronzePlayer.playerX)+Math.abs(o2.pos.y-BronzePlayer.playerY));
    }
    
  };
  
  public final P pos;
  public int item;

  public Table(int x, int y) {
    this.pos = P.get(x, y);
    this.item = 0;
  }

  public boolean hasDish() {
    return (item & Item.DISH) != 0 || (item == Item.EQUIPMENT_DISH);
  }

  public int getDish() {
    return item;
  }

  public boolean hasBlueberries() {
    return (item == Item.BLUEBERRIES || item == Item.EQUIPMENT_BLUEBERRIES);
  }
  public boolean hasIceCream() {
    return (item == Item.ICE_CREAM || item == Item.EQUIPMENT_ICE_CREAM);
  }

  public double dist(int playerX, int playerY) {
    return Math.abs(this.pos.x-playerX)+Math.abs(this.pos.y-playerY);
  }
  @Override
  public String toString() {
    return "at "+pos+" item :"+Item.toString(item);
  }

  private boolean canAddTier1Ingredient(State state, Agent agent) {
    return agent.items == 0;
  }

  private boolean canAddTier2Ingredient(State state, Agent agent) {
    return agent.items == 0 
        || ((agent.items & Item.DISH) != 0          // on a une assiette 
              && (agent.items & item)==0)          // on a pas deja l'item
              && Integer.bitCount(agent.items) < 5  // elle est pas pleine
              ;
  }      
}
