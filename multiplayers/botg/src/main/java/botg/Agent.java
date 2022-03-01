package botg;

import java.util.ArrayList;
import java.util.List;

import botg.units.Hero;
import botg.units.Tower;
import botg.units.Unit;

public class Agent {
  public Tower tower = new Tower();
  public List<Hero> heroes = new ArrayList<>();
  public List<Unit> units = new ArrayList<>();
  
  
  public void clear() {
    units.clear();
    heroes.clear();
  }


  public Hero getHero(int unitId) {
    return heroes.stream().filter(h -> h.unitId == unitId).findFirst().orElse(null);
  }


  public Hero friendOf(Hero hero) {
    if (heroes.size() == 1) return Hero.DEAD_HERO;
    if (heroes.get(0) == hero) return heroes.get(1); else return heroes.get(0);
  }
  
}
