package botg.units;

import java.util.ArrayList;
import java.util.List;

import botg.Action;
import botg.Item;
import botg.Pos;
import botg.State;
import botg.ai.Strategy;

public class Hero extends Base {
  public static final Hero DEAD_HERO = new Hero(null) {
    void init() {
      this.health = -10000;
      this.pos = Pos.from(-100000, -100000); // won't be in range
      
    }
  };  
  
  public String name;
  public State state;
  public int health;
  public int maxHealth;
  public int damage;
  public int mana;
  public Strategy strategy;

  public int[] coolDowns = new int[3];


  public int itemsOwned;
  public List<Item> items = new ArrayList<>();

  public Hero(State state) {
    this.state = state;
    init();
  }

  @Override
  public String toString() {
    return String.format("%s @ %s", name, pos);
  }
  
  void init() {
  }
  
  

  public Action think(State state, int actionIndex) {
    return strategy.think(this, state, actionIndex);
  }

  public boolean nextToEnnemies() {
    for (Hero h : state.opp.heroes) {
      if (h.inRangeForAttack(this)) {
        return true;
      }
    }
    
    for (Unit u : state.opp.units) {
      if (u.inRangeForAttack(this)) {
        return true;
      }
    }
    
    return false;
  }

  public void addItem(Item item) {
    items.add(item);
  }

  public boolean willDie() {
    return this.health < 0.05 * this.maxHealth;
  }

}
