package spring2022.ag;

import spring2022.Hero;
import spring2022.Pos;
import spring2022.State;
import spring2022.Unit;

public class LightState {
  public Unit units[] = new Unit[25];
  public int unitsFE = 0;
  
  public Hero[] hero = new Hero[] { new Hero(), new Hero(), new Hero()};
  public Hero[] oppHeroes = new Hero[] { new Hero(), new Hero(), new Hero()};
  
  public int health;
  public int mana;
  public int wildMana;
  public int extraBonus;
  
  public LightState() {
    for (int i=0;i<units.length;i++) {
      units[i] = new Unit(); // private pool
    }
  }
  
  public void createFrom(State state) {
    createFrom(state, 10_000);
  }
  
  public void createFullFrom(State state) {
    createFrom(state, 500_000);
  }

  public void createFrom(State state, int maxRange) {
    health = state.health[0];
    mana = state.mana[0];
    wildMana = 0;
    
    hero[0].copyFrom(state.myHeroes[0]); hero[0].id = 0;
    hero[1].copyFrom(state.myHeroes[1]); hero[1].id = 1;
    hero[2].copyFrom(state.myHeroes[2]); hero[2].id = 2;

    for (int i=0;i<3;i++) {
      oppHeroes[i].copyFrom(state.oppHeroes[i]);
    }
    
    unitsFE = 0;
    for (int u= 0;u<state.unitsFE;u++) {
      Unit unit = state.fastUnits[u];

      if (unit.isDead()) continue;
      if (!unit.isInRange(State.myBase, maxRange)) continue; // don't get all units ...
      
      this.units[this.unitsFE].copyFrom(unit);
      
      this.unitsFE++;
    }
  }

  public void copyFrom(LightState model) {
    health = model.health;
    mana = model.mana;
    wildMana = 0;
    

    for (int i=0;i<3;i++) {
      hero[i].copyFrom(model.hero[i]);
      oppHeroes[i].copyFrom(model.oppHeroes[i]);
    }
    
    unitsFE = model.unitsFE;
    for (int i=0;i<model.unitsFE;i++) {
      Unit unit = model.units[i];
      this.units[i].copyFrom(unit);
    }
  }

  public Unit getUnitById(int id) {
    for (int i = 0; i < unitsFE; i++) {
      if (units[i].id == id) return units[i];
    }
    return null;
  }
  
  public Pos findPosById(int targetEntity) {
    for (Hero h : hero) {
      if (h.id == targetEntity) return h.pos;
    }
    for (Hero h : oppHeroes) {
      if (h.id == targetEntity) return h.pos;
    }
    for (int i = 0; i < unitsFE; i++) {
      if (units[i].id == targetEntity) return units[i].pos;
    }
    return Pos.VOID;
  }
  
  public void kill(int index) {
    // swap units & decrement unitsFE
    Unit swap = units[index];
    units[index] = units[unitsFE-1];
    units[unitsFE-1] = swap;
    unitsFE--;
  }
  
  
}
