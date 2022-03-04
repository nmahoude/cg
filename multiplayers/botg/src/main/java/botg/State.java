package botg;

import java.util.ArrayList;
import java.util.List;

import botg.ai.StrategyFactory;
import botg.units.Base;
import botg.units.Bush;
import botg.units.Groot;
import botg.units.Hero;
import botg.units.Unit;
import fast.read.FastReader;
import trigonometry.Point;

public class State {
  public static int myTeam;
  
  public Agent me = new Agent();
  public Agent opp = new Agent();
  
  private static List<Hero> allHeroes = new ArrayList<>(); // to reuse them
  public List<Groot> originalGroots = new ArrayList<>();
  
  public List<Bush> bushes = new ArrayList<>();
  public List<Groot> groots = new ArrayList<>();
  public List<Item> items = new ArrayList<>();
  public List<Point> spawnPoints = new ArrayList<>();
  
  int roundType;
  int enemyGold;
  public int gold;

  public void readInit(FastReader in) {
    myTeam = in.nextInt();
    int bushAndSpawnPointCount = in.nextInt(); // useful from wood1, represents the number of bushes and the number of places where neutral units can spawn
    for (int i = 0; i < bushAndSpawnPointCount; i++) {
        String entityType = in.next(); // BUSH, from wood1 it can also be SPAWN
        int x = trans(in.nextInt());
        int y = in.nextInt();
        int radius = in.nextInt();
        
        if ("BUSH".equals(entityType)) {
          Bush bush = new Bush();
          bush.pos = Point.from(State.trans(x), y);
          bush.radius = radius;
        } else if ("SPAWN".equals(entityType)) {
          spawnPoints.add(Point.from(State.trans(x), y));
        }
    }
    
    int itemCount = in.nextInt();
    for (int i = 0; i < itemCount; i++) {
        Item item = Item.from(in);
        items.add(item);
    }
  }

  public void read(FastReader in) {
    clear();
    
    
    gold = in.nextInt();
    enemyGold = in.nextInt();
    roundType = in.nextInt();
    int entityCount = in.nextInt();
    for (int i = 0; i < entityCount; i++) {
      int unitId = in.nextInt();
      int team = in.nextInt();
      String unitType = in.next(); // UNIT, HERO, TOWER, can also be GROOT from wood1
      int x = trans(in.nextInt());
      int y = in.nextInt();
      Point pos = Point.from(x, y);
      int attackRange = in.nextInt();
      int health = in.nextInt();
      int maxHealth = in.nextInt();
      int shield = in.nextInt(); // useful in bronze
      int attackDamage = in.nextInt();
      int movementSpeed = in.nextInt();
      int stunDuration = in.nextInt(); // useful in bronze
      int goldValue = in.nextInt();
      int countDown1 = in.nextInt(); // all countDown and mana variables are useful starting in bronze
      int countDown2 = in.nextInt();
      int countDown3 = in.nextInt();
      int mana = in.nextInt();
      int maxMana = in.nextInt();
      int manaRegeneration = in.nextInt();
      String heroType = in.next(); // DEADPOOL, VALKYRIE, DOCTOR_STRANGE, HULK, IRONMAN
      int isVisible = in.nextInt(); // 0 if it isn't
      int itemsOwned = in.nextInt(); // useful from wood1
      
      Agent agent = team == myTeam ? me : opp;
      
      switch(unitType) {
        case "GROOT":
          Groot groot = originalGroots.stream().filter(g -> g.unitId == unitId).findFirst().orElse(null);
          if (groot == null) {
            groot = new Groot();
            groot.unitId = unitId;
            groot.spawnPoint = pos;
            originalGroots.add(groot);    
          }
          groot.pos = pos;
          groot.range = attackRange;
          this.groots.add(groot);
          break;
        case "TOWER" : 
          
          agent.tower.pos = pos;
          agent.tower.range = attackRange;
          break;
          
        case "HERO" : 
          System.err.println(unitId + " @ "+pos);
          
          Hero hero = allHeroes.stream().filter(h -> h.unitId == unitId).findFirst().orElse(null);
          if (hero == null) {
            hero = new Hero(this);
            allHeroes.add(hero);
            hero.strategy = StrategyFactory.forHero(heroType);
          }
          
          hero.unitId = unitId;
          hero.name = heroType;
          hero.pos = pos;
          hero.health = health;
          hero.maxHealth = maxHealth;
          hero.range = attackRange;
          hero.damage = attackDamage;
          hero.itemsOwned = itemsOwned;
          hero.mana = mana;
          hero.maxMana = maxMana;
          hero.setMovementSpeed(movementSpeed);
          
          hero.coolDowns[0] = countDown1;
          hero.coolDowns[1] = countDown2;
          hero.coolDowns[2] = countDown3;
          
          agent.heroes.add(hero);
          break;
        case "UNIT":
            Unit unit = new Unit();
            unit.unitId = unitId;
            unit.pos = pos;
            unit.health = health;
            
            agent.units.add(unit);
            break;
          default: 
            System.err.println("*** TODO *** "+ unitType);
      }
      

    }
  }

  private void clear() {
    groots.clear();
    me.clear();
    opp.clear();
  }

  static int trans(double x) {
    if (myTeam == 1) return (int)(1920 - x); else return (int)x;
  }

  public double enemyLine() {
    double maxX = -1;
    for (Base unit : this.me.units) {
      if (unit.pos.x > maxX)
        maxX = unit.pos.x;
    }
    return maxX;
  }
}
