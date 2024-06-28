package spring2022;

import fast.read.FastReader;
import spring2022.ag.AG;
import spring2022.ai.AggroAI;
import spring2022.ai.TriAction;

public class Player {
  private static final int WARMUP_TIME = 600;
  public static boolean inversed = false;
  
  public static int turn = 0;
  public static long start;
  
  public State state = new State();
  public AggroAI ai = new AggroAI();
  
  public AG ag = new AG();
  
  public static int ennemyAttaquantsCount;
  public static int[] ennemyId = new int[3];
  public static int ennemyIdFE = 0;
  
  static Pos[] predictedPositions = new Pos[] { new Pos(), new Pos(), new Pos() };
  public static boolean[] danger = new boolean[3];
  public static boolean globalDanger = false;
  
  public static void main(String[] args) {
    FastReader in = new FastReader(System.in);

    new Player().play(in);
  }

  private void play(FastReader in) {
    readGlobal(in);
    
    while(true) {
      UnitPool.reset();
      ActionPool.reset();
      
      readTurn(in);
      
      think();
    }
  }

  private void think() {
    if (turn > 10) {
      for (int i=0;i<2;i++) {
        globalDanger = true;
        if (state.myHeroes[i].pos.dist(predictedPositions[i]) > 10 /* prendre une marge avec les calculs de CG ...*/) {
          System.err.println("DANGERP :  Hero "+state.myHeroes[i]+" a pris un sort, aurait du etre en  "+predictedPositions[i]);
          danger[i] = true;
        } else {
          if (danger[i]) {
            System.err.println("DANGERP : Hero "+state.myHeroes[i]+" retourne à la normale");
          }
          danger[i] = false;
          globalDanger = false;
        }
      }
    }
    
    TriAction action = ai.think(state);
    
    for (int i=0;i<3;i++) {
      if (action.actions[i].type == Action.TYPE_MOVE) {
        predictedPositions[i].copyFrom(action.actions[i].target);
      } else {
        predictedPositions[i].copyFrom(state.myHeroes[i].pos);
      }
    }
    
    action.output();
  }

  public void readGlobal(FastReader in) {
    state.readGlobal(in);
  }
  
  public void readTurn(FastReader in) {
    Player.turn++;
    state.read(in);

    updateEnnemyId();
    System.err.println("turn "+ Integer.toString(turn));
    
    if (turn == 1) {
      Player.start = System.currentTimeMillis() + WARMUP_TIME;
    } else {
      Player.start = System.currentTimeMillis();
    }
  }
  
  private void updateEnnemyId() {
    ennemyIdFE = 0;
    
    int ennemyCount = 0;
    for (int i=0;i<3;i++) {
      Hero opp = state.oppHeroes[i];
      if (opp.isInFog()) continue;
      //if (opp.isControlled) continue;
      
      // in range of base
      if (opp.pos.fastDist(State.myBase) < State.BASE_TARGET_DIST + 2000) {
        ennemyCount++;
        for (int h=0;h<2;h++) {
          // in range of a hero
          if (opp.isInRange(state.myHeroes[h], State.CONTROL_RANGE)) {
            ennemyId[ennemyIdFE++] = opp.id;
          }
        }
      }
    }
    ennemyAttaquantsCount = Math.max(ennemyCount, ennemyAttaquantsCount); // count them 
  }
}
