package spring2022;


import java.util.Arrays;

import fast.read.FastReader;

public class State {
  public static boolean DEBUG_INPUTS = true;
  
  public static final int WIDTH = 17630;
  public static final int HEIGHT = 9000;

  public static final int HEROES_COUNT = 3;
  public static final int HERO_MAX_MOVE = 800;

  public static final int MOB_MOVE = 400;

  public static final int BASE_TARGET_DIST = 5000;
  public static final int BASE_TARGET_DIST2 = BASE_TARGET_DIST * BASE_TARGET_DIST;
  public static final int BASE_KILL_RADIUS = 300;
  public static final int BASE_TARGET_KILL2 = BASE_KILL_RADIUS * BASE_KILL_RADIUS;

  private static final int BASE_VIEW = BASE_TARGET_DIST + 1000;
  
  public static final int MONSTER_TARGET_KILL = 800;
  public static final int MONSTER_TARGET_KILL2 = 800 * 800;
  
  public static final int SHIELD_LIFE = 12;
  
  public static final int HERO_VIEW_RADIUS = 2200;
  public static final int HERO_VIEW_RADIUS2 = HERO_VIEW_RADIUS * HERO_VIEW_RADIUS;

  public static final int WIND_RANGE = 1280;
  public static final int WIND_RANGE2 = WIND_RANGE * WIND_RANGE;
  public static final int WIND_PUSH_DISTANCE = 2200;

  public static final int SHIELD_RANGE = 2200;

  public static final int CONTROL_RANGE = 2200;
  public static final int CONTROL_RANGE2 = CONTROL_RANGE * CONTROL_RANGE;

  public static final Pos myBase = new Pos(0, 0);
  public static final Pos oppBase = new Pos(WIDTH, HEIGHT);
  public static final Hero oppAttacker = new Hero(); // to keep where is attacker
  public static Future future = new Future();
  private static FogResolver fogResolver = new FogResolver();

  
  public Hero[] allHeroes = new Hero[6];
  public Hero[] myHeroes = new Hero[3];
  public Hero[] oppHeroes = new Hero[3];

  public Unit[] fastUnits = new Unit[400];
  public int unitsFE;
  
  public int[] health = new int[2];
  public int[] mana = new int[2];
  
  public State() {
    for (int i=0;i<6;i++) {
      allHeroes[i] = new Hero();
    }
    setHeroesAndUnits();
    
    myBase.x = 0;
    myBase.y = 0;
    oppBase.x = WIDTH;
    oppBase.y = HEIGHT;
  }
  
  public void readGlobal(FastReader in) {
    int baseX = in.nextInt();
    int baseY = in.nextInt();
    
    Player.inversed = baseX != 0;
    setHeroesAndUnits();

    int heroesPerPlayer = in.nextInt();

    if (State.DEBUG_INPUTS) System.err.println("^"+baseX+" "+baseY+" "+heroesPerPlayer);
  }

  private void setHeroesAndUnits() {
    if (Player.inversed) {
      for (int i=0;i<3;i++) {
        myHeroes[i] = allHeroes[3+i];
        myHeroes[i].owner = 0;

        oppHeroes[i] = allHeroes[i];
        oppHeroes[i].owner = 1;
      } 
    } else {
      for (int i=0;i<3;i++) {
        myHeroes[i] = allHeroes[i];
        myHeroes[i].owner = 0;
        
        oppHeroes[i] = allHeroes[3+i];
        oppHeroes[i].owner = 1;
      } 
    }
  }

  public void read(FastReader in) {
    fogResolver.startOfturn(this);
    
    for (Hero hero : allHeroes) {
      hero.setInFog();
    }
    
    for (int i = 0; i < 2; i++) {
      int health = in.nextInt(); 
      int mana = in.nextInt();
      this.health[i] = health;
      this.mana[i] = mana; 

    }
    if (State.DEBUG_INPUTS) System.err.println("^"+health[0]+" "+mana[0]+" "+health[1]+" "+mana[1]);

    int entityCount = in.nextInt(); // Amount of heros and monsters you can see
    if (State.DEBUG_INPUTS) System.err.println("^"+entityCount);

    unitsFE = 0;
    for (int i = 0; i < entityCount; i++) {
      int id = in.nextInt();
      int type = in.nextInt();

      if (type == 1 || type == 2) {
        allHeroes[id].id = id;
        allHeroes[id].read(in);
      } else {
        Unit unit = UnitPool.get(); // Attention le pool est reset entre chaque tour
        unit.id = id;
        unit.read(in);
        fastUnits[unitsFE++] = unit;

      }
    }
    
    updateOppAttacker();

    fogResolver.endOfRead(this);
    fogResolver.debug();
    fogResolver.reinsertFoMobs(this);

    updateAfterFog();
  }

  private void updateAfterFog() {
    future.calculate(this);
  }

  private void updateOppAttacker() {
    Hero turnOppAttacker = attackerOppHero();
    if (turnOppAttacker != null) {
      oppAttacker.copyFrom(turnOppAttacker);
    } else {
      oppAttacker.pos.copyFrom(Pos.VOID);
      oppAttacker.shieldLife = 1000; 
    }
  }
  
  public void copyFrom(State model) {
    this.health[0] = model.health[0];
    this.health[1] = model.health[1];
    this.mana[0] = model.mana[0];
    this.mana[1] = model.mana[1];
    
    
    for (int i=0;i<6;i++) {
      this.allHeroes[i].copyFrom(model.allHeroes[i]);
    }
    // attach my & opp heroes correctly
    for (int i=0;i<3;i++) {
      if (Player.inversed) {
        myHeroes[i] = allHeroes[i+3];
        oppHeroes[i] = allHeroes[i];
      } else {
        myHeroes[i] = allHeroes[i];
        oppHeroes[i] = allHeroes[i+3];
      }
    }

    unitsFE = 0;
    for (int i = 0; i < model.unitsFE; i++) {
      Unit u = model.fastUnits[i];
      Unit unit = UnitPool.get();
      unit.copyFrom(u);
      fastUnits[unitsFE++] = unit;
    }
  }

  public Unit getClosestUnit(Pos pos) {
    int bestDist = Integer.MAX_VALUE;
    Unit best = null;
    
    for (int u= 0;u<unitsFE;u++) {
      Unit unit = fastUnits[u];
      if (unit.isDead()) continue;
      int dist2 = pos.dist2(unit.pos);
      if (dist2 < bestDist) {
        bestDist = dist2;
        best = unit;
      }
    }
    
    return best;
  }

  /** 
   * return the best direction to throw a mob depending on occupation 
   */
  final int[] qCount = new int[3];
  public Pos getBestQuadrantToThrow() {
    for (int i=0;i<qCount.length;i++) {
      qCount[i] = 0;
    }
    
    // check quadrant
    for (int u= 0;u<unitsFE;u++) {
      Unit monster = fastUnits[u];
      if (monster.isDead()) continue;
      if (!monster.isInRange(oppBase, State.BASE_TARGET_DIST)) continue;

      int q = getQuadrant(monster.pos);
      qCount[q]++;
    }
    int leastQuandrant = 1; 
    for (int i=0;i<3;i++) {
      if (qCount[i] < qCount[leastQuandrant]) {
        leastQuandrant = i;
      }
    }

    System.err.println("DEBUG Quadrants : " + Arrays.toString(qCount));
    System.err.println("         least is "+leastQuandrant);
    if (leastQuandrant == 1) {
      return oppBase;
    } else if (leastQuandrant == 0) {
      return Pos.get(17630-1 - 3000, 9000-1 );
    } else {
      return Pos.get(17630-1, 9000-1 - 3001);
    }
  }
  
  public static int getQuadrant(Pos pos) {
    double x = oppBase.x - pos.x;
    double y = oppBase.y - pos.y;
    // project on oppBase -> 
    
    int dist = (int) Math.sqrt(x * x + y * y);
    
    x = x / dist;
    if (x > Math.cos(Math.PI / 6)) {
      return 0;
    } else if (x > Math.cos(2 * Math.PI / 6)) {
      return 1;
    } else
      return 2;
  }

  /**
   * return opp hero that is in base view zone (considering he is attacking me)
   */
  private Hero attackerOppHero() {
    for (int i=0;i<3;i++) {
      if (myBase.isInRange(oppHeroes[i].pos, BASE_VIEW)) return oppHeroes[i];
    }
    
    return null;
  }

  public Unit findUnitById(int id) {
    for (int i = 0; i < unitsFE; i++) {
      Unit unit = fastUnits[i];
      if (unit.id== id) return unit;
    }
    return null;
  }

  public int getUnitCountInRange(Pos pos, int range) {
    int count = 0;
    for (int i = 0; i < unitsFE; i++) {
      Unit unit = fastUnits[i];
      if (unit.isDead()) continue;
      if (unit.isInRange(pos, range)) count++;
    }
    return count;
  }

  /** for debugging purposes */
  public void readFog(FastReader in) {
    int entityInFog = in.nextInt();
    for (int i=0;i<entityInFog;i++) {
      int id = in.nextInt();
      int type = in.nextInt();

      Unit unit = new Unit(); 
      unit.id = id;
      unit.read(in);
      fastUnits[unitsFE++] = unit;

    }
    
    updateAfterFog();
  }

  public Hero findHeroById(int id) {
    return allHeroes[id];
  }

  public Pos findPosById(int targetEntity) {
    for (Hero h : allHeroes) {
      if (h.id == targetEntity) return h.pos;
    }
    for (int u= 0;u<unitsFE;u++) {
      Unit unit = fastUnits[u];
      if (unit.id == targetEntity) return unit.pos;
    }
    return null;
  }

  public static State fromInput(String input) {
    return fromInput(false, input);
  }
  
  public static State fromInput(boolean inversed, String input) {
    String lines[] = input.split("\n");
    
    String gameInput = "";
    String fogInput = "";
    String attackerInput = "";
    int i = 0;
    
    
    
    while (lines[i].trim().startsWith("^")) {
      gameInput += lines[i++]+"\r\n";
    }
    while (!lines[i++].contains("^"));
    i--;
    
    while (lines[i].trim().startsWith("^")) {
      fogInput += lines[i++]+"\r\n";
    }

    while (!lines[i++].contains("^"));
    i--;
    
    while (i < lines.length && lines[i].trim().startsWith("^")) {
      attackerInput += lines[i++]+"\r\n";
    }
    
    State state = new State();
    if (inversed) {
      state.readGlobal(FastReader.fromString("1 1 3 "));
    } else {
      state.readGlobal(FastReader.fromString("0 0 3 "));
    }
    
    state.read(FastReader.fromString(gameInput.replace("^", "") ));
    state.readFog(FastReader.fromString(fogInput.replace("^", "") ));
    
    // if (attacker != null) attacker.read(FastReader.fromString(attackerInput));
    
    return state;
  }

  public void addFogMob(Unit anotherMob) {
    Unit unit = UnitPool.get();
    unit.copyFrom(anotherMob);
    fastUnits[unitsFE++] = unit;
  }
}
  