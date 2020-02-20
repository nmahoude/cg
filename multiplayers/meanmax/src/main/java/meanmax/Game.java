package meanmax;

import java.util.Random;
import java.util.Scanner;

import meanmax.ai.ag2.AG;
import meanmax.ai.dummy.DummyAG;
import meanmax.entities.Entity;
import meanmax.entities.SkillEffect;
import meanmax.entities.Tanker;
import meanmax.entities.Wreck;
import trigo.Position;

public class Game {
  public static boolean DEBUG_INPUT = false;
  public static boolean DEBUG_AI = false;

  public static Random random = new Random(System.currentTimeMillis());
  
  private static final int MAX_ENTITIES = 1000;
  public static final int REAPER = 0;
  public static final int DESTROYER = 1;
  public static final int DOOF = 2;
  public static final int TANKER = 3;
  public static final int WRECK = 4;
  public static final int SKILL_EFFECT_TAR = 5;
  public static final int SKILL_EFFECT_OIL = 6;
  public static final int SKILL_EFFECT_GRENADE = 10;
  
  public static final Position WATERTOWN = new Position(0,0);
  public static final double MAP_RADIUS = 6000.0;
  public static final double MAP_RADIUS_2= MAP_RADIUS * MAP_RADIUS;
  public static final double MAP_DIAMETER = 2 * MAP_RADIUS;
  public static final double MAP_DIAMETER_2 = MAP_DIAMETER * MAP_DIAMETER;
  public static final double WATERTOWN_RADIUS = 3000.0;
  public static final double WATERTOWN_RADIUS_2 = WATERTOWN_RADIUS * WATERTOWN_RADIUS;
  public static double REAPER_SKILL_MASS_BONUS = 10.0;
  
  public static Player players[] = new Player[3];
  
  public static Entity entities[];
  public static int entities_FE = 0;
  public static int b_entitiesFE = 0;
  
  public static Tanker tankers[] = new Tanker[100];
  public static int tankers_FE = 0;
  public static int b_tanker_FE = 0;
  public static Wreck wrecks[] = new Wreck[100];
  public static int wrecks_FE = 0;
  public static int b_wreck_FE = 0;

  public static SkillEffect skillEffects[] = new SkillEffect[100];
  public static int skillEffects_FE = 0;
  public static int b_skillEffect_FE = 0;

  public static SkillEffect seDoofs[] = new SkillEffect[9];
  public static int seDoofs_FE = 0;
  public static int b_seDoofs_FE = 0;

  public static int turn;
  
  public static long start;
  public static int reaperFreeSpots;
  public static DummyAG dummy = new DummyAG();

  static {
    globalInit();
  }
  
  
  public static void play(Scanner in) {
    //Wait ai = new Wait();
    AG ai = new AG();
    //BeamEvaluator evaluator = new BeamEvaluator();
    // game loop
    turn = 0;
    while (true) {
      turnInit();
      
      readInput(in);

      System.err.println("Current eval : " + ai.eval.eval());
      //ai.compareExpected();
      ai.think(Game.players[0]);
      //ai.saveExpected();
      
      ai.output();
      System.err.println("time : "+(System.currentTimeMillis()-start));
    }
  }

  private static void readInput(Scanner in) {
    players[0].score = in.nextInt();
    start = System.currentTimeMillis();
    players[1].score = in.nextInt();
    players[2].score = in.nextInt();
    for (int i=0;i<3;i++) {
      players[i].rage = in.nextInt();
    }
    
    int unitCount = in.nextInt();
    for (int i = 0; i < unitCount; i++) {
      readOneUnit(in);
    }
    backup();
  }

  public static void fullBackup() {
    backup();
    for (int i=0;i<entities_FE;i++) {
      entities[i].backup();
    }
  }
  public static void backup() {
    b_entitiesFE = entities_FE;
    b_skillEffect_FE = skillEffects_FE;
    b_seDoofs_FE = seDoofs_FE;
    b_wreck_FE = wrecks_FE;
    b_tanker_FE = tankers_FE;

    players[0].backup();
    players[1].backup();
    players[2].backup();
    
    // entities are backup when read
  }

  public static Entity readOneUnit(Scanner in) {
    int unitId = in.nextInt();
    int unitType = in.nextInt();
    int player = in.nextInt();
    if (Game.DEBUG_INPUT) {
      System.err.print("read("+unitId+","+unitType+","+player+",");
    }
    
    Entity entity = null;
    switch(unitType) {
      case REAPER:
        players[player].reaper.read(in);
        entity = players[player].reaper;
        break;
      case DESTROYER:
        players[player].destroyer.read(in);
        entity = players[player].destroyer;
        break;
      case DOOF:
        players[player].doof.read(in);
        entity = players[player].doof;
        break;
      case TANKER:
        tankers[tankers_FE].read(in);
        entities[entities_FE++] = tankers[tankers_FE];
        entity = tankers[tankers_FE++];
        break;
      case WRECK:
        wrecks[wrecks_FE].read(in);
        entities[entities_FE++] = wrecks[wrecks_FE];
        entity = wrecks[wrecks_FE++];
        break;
      case SKILL_EFFECT_TAR:
        skillEffects[skillEffects_FE].type= unitType;
        skillEffects[skillEffects_FE].read(in);
        entities[entities_FE++] = skillEffects[skillEffects_FE];
        entity = skillEffects[skillEffects_FE++];
        break;
      case SKILL_EFFECT_OIL:
        SkillEffect skillEffect = skillEffects[skillEffects_FE];
        skillEffect.type= unitType;
        skillEffects[skillEffects_FE].read(in);

        seDoofs[seDoofs_FE++] = skillEffect;
        entities[entities_FE++] = skillEffect;
        entity = skillEffect;
        
        skillEffects_FE++;
        break;
    }
    entity.unitId = unitId;
    
    return entity;
  }

  public static void restore() {
    entities_FE = b_entitiesFE;
    skillEffects_FE = b_skillEffect_FE;
    seDoofs_FE = b_seDoofs_FE;
    wrecks_FE = b_wreck_FE;
    tankers_FE = b_tanker_FE;

    players[0].restore();
    players[1].restore();
    players[2].restore();

    for (int i=0;i<Game.entities_FE;i++) {
      Game.entities[i].restore();
    }
  }

  public static void turnInit() {
    turn++;
    entities_FE = 9; // keep the inmovable entities
    wrecks_FE = tankers_FE = skillEffects_FE = seDoofs_FE = 0;
  }

  public static void globalInit() {
    entities = new Entity[MAX_ENTITIES];
    entities_FE = 0;
    
    for (int i = 0;i<3;i++) {
      players[i] = new Player();
      players[i].index = i;
      
      entities[entities_FE++] = players[i].reaper;
      entities[entities_FE++] = players[i].destroyer;
      entities[entities_FE++] = players[i].doof;
    }
    for (int i=0;i<100;i++) {
      tankers[i] = new Tanker();
      wrecks[i] = new Wreck();
      skillEffects[i] = new SkillEffect();
    }
  }

  public static void cleanup() {
    entities_FE = 9;
    seDoofs_FE = 0;

    tankers_FE = compileArray(tankers, tankers_FE);
    wrecks_FE = compileArray(wrecks, wrecks_FE);
    skillEffects_FE = compileArray(skillEffects, skillEffects_FE);
    
    for (int i=0;i<tankers_FE;i++) {
      entities[entities_FE++] = tankers[i];
    }
    for (int i=0;i<wrecks_FE;i++) {
      entities[entities_FE++] = wrecks[i];
    }
    for (int i=0;i<skillEffects_FE;i++) {
      entities[entities_FE++] = skillEffects[i];
      if (skillEffects[i].type == Game.SKILL_EFFECT_OIL) {
        seDoofs[seDoofs_FE++] = skillEffects[i];
      }
    }
  }

  private static int compileArray(Entity[] array, int fe) {
    int newFE = 0;
    for (int i=0;i<fe;i++) {
      int nextUndead = i;
      Entity entity = array[i];
      if (entity.dead) {
        nextUndead = i+1;
        while (nextUndead < fe && array[nextUndead].dead) nextUndead++;
      }
      if (nextUndead<fe) {
        Entity tmp = array[nextUndead];
        array[nextUndead] = entity;
        array[newFE++] = tmp;
      }
    }
    return newFE;
  }
}
