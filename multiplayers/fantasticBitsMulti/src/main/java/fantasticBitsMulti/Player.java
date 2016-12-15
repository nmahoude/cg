package fantasticBitsMulti;

import java.util.Scanner;

import fantasticBitsMulti.ag.AGSolution;
import fantasticBitsMulti.spells.Spell;
import fantasticBitsMulti.units.Bludger;
import fantasticBitsMulti.units.EntityType;
import fantasticBitsMulti.units.Pole;
import fantasticBitsMulti.units.Snaffle;
import fantasticBitsMulti.units.Unit;
import fantasticBitsMulti.units.Wizard;
import random.FastRand;
import trigonometry.Point;

public class Player {
  private static final int COLLISION_SIZE = 100000;
  static final double TO_RAD = Math.PI / 180.0;
  public static final int ANGLES_LENGTH = 36;
  static final double ANGLES[] = new double[]{0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0, 110.0, 120.0, 130.0, 140.0, 150.0, 160.0, 170.0, 180.0, 190.0, 200.0, 210.0, 220.0, 230.0, 240.0, 250.0, 260.0, 270.0, 280.0, 290.0, 300.0, 310.0, 320.0, 330.0, 340.0, 350.0};
  public static final int DEPTH = 4;
  static final double COEF_PATIENCE = 0.9;
  public static final double E = 0.00001;
  public static final int SPELL_DEPTH = 8;
  private static final int POOL = 50;
  private static final int MUTATION = 2;
  public static double cosAngles[] = new double[ANGLES_LENGTH];
  public static double sinAngles[] = new double[ANGLES_LENGTH];
  static double patiences[] = new double[DEPTH];
  
  public static FastRand rand;
  public static int myTeam;
  

  static Collision fake;
  public static int collisionsCacheFE = 0;
  public static Collision[] collisionsCache;
  static int collisionsFE = 0;
  static Collision[] collisions;
  static int tempCollisionsFE = 0;
  static Collision[] tempCollisions;
  
  public static Wizard[] wizards = new Wizard[4];
  static Unit unitsById[] = new Unit[24];
  static int unitsFE = 0;
  private static Unit[] units = new Unit[20];
  public static Wizard myWizard1;
  public static Wizard myWizard2;
  public  static Wizard hisWizard1;
  public static Wizard hisWizard2;
  public static Point myGoal;
  public static Point hisGoal;
  private static Point mid;
  private static Bludger[] bludgers = new Bludger[2];
  private static Pole[] poles = new Pole[4];
  
  static Spell spells[] = new Spell[16];
  public static Unit spellTargets[][] = new Unit[4][20];
  public static int spellTargetsFE[] = new int[4];

  
  public static int mana;
  public static int myScore;
  public static int hisScore;
  
  public static int smana;
  public static int smyScore;
  public static int shisScore;
  
  
  public static Snaffle[] snaffles = new Snaffle[10];
  public static int snafflesFE;
  private static long start;
  private static int turn = 0;
  private static int victory;
  private static int oldSnafflesFE;
  
  
  static double energy = 0;
  static int depth = 0;

  private static AGSolution best = new AGSolution();

  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    myTeam = in.nextInt();

    rand = new FastRand(42);
    fake = new Collision();
    fake.t = 1000.0;
    
    initConstants();
    initCollisionsCache();
    
    createWizards();
    createBludgers();
    createPoles();
    unitsFE = 10;

    int spellsFE = 0;
    for (int i = 0; i < 4; ++i) {
      for (int j = 0; j < 4; ++j) {
        spells[spellsFE++] = wizards[j].spells[i];
      }
    }
   
    while (true) {
      int myScore = in.nextInt();
      int myMagic = in.nextInt();
      int opponentScore = in.nextInt();
      int opponentMagic = in.nextInt();
      int entities = in.nextInt();
      
      System.err.println("Number of entities : "+entities);
      start = System.nanoTime();
      int bludgersFE = 0;
      if (turn  != 0) {
        for (int i = 0; i < 24; ++i) {
          Unit u = unitsById[i];

          if (u != null && u.type == EntityType.SNAFFLE) {
            u.dead = true;
            u.carrier = null;
          }
        }
      }
      
      for (int i = 0; i < entities; i++) {
        int id = in.nextInt();
        String entity = in.next();
        System.err.println("Entity : "+entity);
        EntityType entityType = EntityType.valueOf(entity);
        int x = in.nextInt();
        int y = in.nextInt();
        int vx = in.nextInt();
        int vy = in.nextInt();
        int state = in.nextInt();
        
        Unit unit = null;
        if (entityType == EntityType.WIZARD || entityType == EntityType.OPPONENT_WIZARD)  {
          unit = wizards[id];
        } else if (entityType == EntityType.SNAFFLE) {
          if (turn == 0) {
            unit = new Snaffle();
          } else {
            unit = unitsById[id];
          }
//          if (unit == null) {
//            for (int u=0;u<unitsFE;u++) {
//              if (unitsById[u] != null)
//                System.err.print(" "+unitsById[u].id);
//              else {
//                System.err.print(" "+u+" is null");
//              }
//            }
//            System.err.println("");
//          }
          unit.dead = false;
          units[unitsFE++] = unit;
          snaffles[snafflesFE++] = (Snaffle)unit;
        } else if (entityType == EntityType.BLUDGER) {
          unit = bludgers[bludgersFE++];
        }
        unit.update(id, x, y, vx, vy, state);
      }
      
      if (turn == 0) {
        victory = (snafflesFE / 2 ) + 1;
        for (int i=0;i<unitsFE;++i) {
          unitsById[units[i].id] = units[i];
        }
      }
      // Mise à jour des carriers et des snaffles
      for (int i = 0; i < 4; ++i) {
        wizards[i].updateSnaffle();
      }
      updateScore();
      updateBludgersSpells();
      updatePetrificus();
      updateSnaffleSpells();
      
      for (int i = 0; i < unitsFE; ++i) {
        units[i].save();

        smana = mana;
        smyScore = myScore;
        shisScore = hisScore;
      }

      for (int i = 0; i < 16; ++i) {
        spells[i].reloadTarget();
        spells[i].save();
      }
      evolution();
     
      myWizard1.output(best.moves1[0], best.spellTurn1, best.spell1, best.spellTarget1);
      myWizard2.output(best.moves2[0], best.spellTurn2, best.spell2, best.spellTarget2);

    }
  }

  private static void evolution() {
    AGSolution base = null;
    
    if (turn != 0) {
      base = new AGSolution();
      for (int j = 1; j < DEPTH; ++j) {
        base.moves1[j - 1] = best.moves1[j];
        base.moves2[j - 1] = best.moves2[j];
        base.spellTurn1 = best.spellTurn1;
        base.spell1 = best.spell1;
        base.spellTarget1 = best.spellTarget1;
        base.spellTurn2 = best.spellTurn2;
        base.spell2 = best.spell2;
        base.spellTarget2 = best.spellTarget2;
        if (base.spellTurn1 == 0) {
          base.spellTurn1 = SPELL_DEPTH - 1;
        } else {
          base.spellTurn1 -= 1;
        }

        if (base.spellTurn2 == 0) {
          base.spellTurn2 = SPELL_DEPTH - 1;
        } else {
          base.spellTurn2 -= 1;
        }

        if (base.spellTarget1.dead) {
          base.spellTurn1 = SPELL_DEPTH - 1;
          base.spellTarget1 = spellTargets[base.spell1][rand.fastRandInt(spellTargetsFE[base.spell1])];
        }

        if (base.spellTarget2.dead) {
          base.spellTurn2 = SPELL_DEPTH - 1;
          base.spellTarget2 = spellTargets[base.spell2][rand.fastRandInt(spellTargetsFE[base.spell2])];
        }
      }
    }
    
    AGSolution[] pool = new AGSolution[POOL];
    AGSolution[] newPool = new AGSolution[POOL];
    AGSolution[] temp;
    int counter = POOL;
    
    best = new AGSolution();
    AGSolution sol = new AGSolution();
    sol.randomize();
    simulate(sol);
    best.copy(sol);
    pool[0] = sol;
    best.copy(sol);
    AGSolution tempBest = sol;
    // First generation
    int startI = 1;
    if (turn != 0) {
      // Populate the POOL with some copy of the previous best one
      for (int i = startI; i < POOL / 5; ++i) {
        AGSolution solution = new AGSolution();
        solution.copy(base);
        // Add a last one random
        solution.moves1[DEPTH - 1] = rand.fastRandInt(ANGLES_LENGTH);
        solution.moves2[DEPTH - 1] = rand.fastRandInt(ANGLES_LENGTH);
        simulate(solution);
        if (solution.energy > tempBest.energy) {
          tempBest = solution;
        }
        pool[i] = solution;
      }
      startI = POOL / 5;
    }
    for (int i = startI; i < POOL; ++i) {
      AGSolution solution = new AGSolution();
      solution.randomize();

      simulate(solution);

      if (solution.energy > tempBest.energy) {
        tempBest = solution;
      }

      pool[i] = solution;
    }

    if (tempBest.energy > best.energy) {
      best.copy(tempBest);
    }
    tempBest = best;

    double limit = turn != 0 ? 85_000_000 : 800_000_000;
    int generation = 1;
    int bestGeneration = 1;
    int poolFE;
    while (System.nanoTime() - start < limit) {
      // New generation

      // Force the actual best with a mutation to be in the pool
      AGSolution solution = new AGSolution();
      solution.copy(tempBest);
      solution.mutate();
      simulate(solution);

      if (solution.energy > tempBest.energy) {
        tempBest = solution;
      }

      newPool[0] = solution;

      counter += 1;

      poolFE = 1;
      while (poolFE < POOL && System.nanoTime() - start < limit) {
        int aIndex = rand.fastRandInt(POOL);
        int bIndex;

        do {
          bIndex = rand.fastRandInt(POOL);
        } while (bIndex == aIndex);

        int firstIndex = pool[aIndex].energy > pool[bIndex].energy ? aIndex : bIndex;

        do {
          aIndex = rand.fastRandInt(POOL);
        } while (aIndex == firstIndex);

        do {
          bIndex = rand.fastRandInt(POOL);
        } while (bIndex == aIndex && bIndex != firstIndex);

        int secondIndex = pool[aIndex].energy > pool[bIndex].energy ? aIndex : bIndex;

        AGSolution child = pool[firstIndex].merge(pool[secondIndex]);

        if (rand.fastRandInt(MUTATION) == 0) {
          child.mutate();
        }

        simulate(child);

        if (child.energy > tempBest.energy) {
          tempBest = child;
        }

        newPool[poolFE++] = child;

        counter += 1;
      }

      // Burn previous generation !!
//      for (int i = 0; i < POOL; ++i) {
//        delete pool[i]; // TODO WTF ?
//      }

      temp = pool;
      pool = newPool;
      newPool = temp;

      if (tempBest.energy > best.energy) {
        best.copy(tempBest);
        bestGeneration = generation;
      }
      tempBest = best;

      generation += 1;
    }
    
    // Play a last time to check some infos
    myWizard1.apply(best, 0, 1);
    myWizard2.apply(best, 0, 2);
    dummies();

    play();
    
    smana = mana;
    bludgers[0].slast = bludgers[0].last;
    bludgers[1].slast = bludgers[1].last;

    for (int i = 0; i < 16; ++i) {
      spells[i].save();
    }
    reset();
    
    // Burn last generation !!
    //TODO revoir les deletes ...
//    for (int i = 0; i < poolFE; ++i) {
//      delete pool[i];
//    }
//
//    delete [] pool;
//    delete [] newPool;

    turn += 1;
    unitsFE = 10;

    oldSnafflesFE = snafflesFE;
    snafflesFE = 0;
  }

  private static void reset() {
    for (int i = 0; i < unitsFE; ++i) {
      units[i].reset();
    }

    for (int i = 0; i < 16; ++i) {
      spells[i].reset();
    }

    mana = smana;
    myScore = smyScore;
    hisScore = shisScore;
  }

  private static void play() {
    for (int i = 0; i < 4; ++i) {
      spells[i].apply();
    }

    bludgers[0].play();
    bludgers[1].play();
    wizards[0].play();
    wizards[1].play();
    wizards[2].play();
    wizards[3].play();

    for (int i = 5; i < 16; ++i) {
      spells[i].apply();
    }

    move();

    for (int i = 0; i < unitsFE; ++i) {
      units[i].end();
    }

    if (mana != 100) {
      mana += 1;
    }

  }

  private static void move() {
    double t = 0.0;
    double delta;

    Collision next = fake;
    collisionsCacheFE = 0;
    collisionsFE = 0;
    tempCollisionsFE = 0;

    Collision col;
    Unit a;
    Unit b;
    Unit u;
    int i, j;

    // Get first collisions
    for (i = 0; i < unitsFE; ++i) {
      a = units[i];

      col = a.collision(t);

      if (col != null) {
        collisions[collisionsFE++] = col;

        if (col.t < next.t) {
          next = col;
        }
      }

      for (j = i + 1; j < unitsFE; ++j) {
        b = units[j];

        if (a.can(b)) {
          col = a.collision(b, t);

          if (col != null) {
            collisions[collisionsFE++] = col;

            if (col.t < next.t) {
              next = col;
            }
          }
        }
      }
    }

    while (t < 1.0) {
      if (next == fake) { // no collision found
        for (i = 0; i < unitsFE; ++i) {
          units[i].move(1.0 - t);
        }

        break;
      } else {
        // Move to the collision time
        delta = next.t - t;
        for (i = 0; i < unitsFE; ++i) {
          units[i].move(delta);
        }

        t = next.t;

        if (next.dir != 0) {
          next.a.bounce(next.dir);
        } else {
          next.a.bounce(next.b);
        }

        a = next.a;
        b = next.b;

        // Invalid previous collisions for the concerned units and get new ones
        next = fake;

        for (i = 0; i < collisionsFE; ++i) {
          col = collisions[i];

          if (!mustErase(col, a, b)) {
            if (col.t < next.t) {
              next = col;
            }

            tempCollisions[tempCollisionsFE++] = col;
          }
        }

        Collision[] temp = tempCollisions;
        tempCollisions = collisions;
        collisions = temp;

        collisionsFE = tempCollisionsFE;
        tempCollisionsFE = 0;

        // Find new collisions for a
        col = a.collision(t);
        if (col != null) {
          collisions[collisionsFE++] = col;

          if (col.t < next.t) {
            next = col;
          }
        }

        for (i = 0; i < unitsFE; ++i) {
          u = units[i];

          if (a.id != u.id && a.can(u)) {
            col = a.collision(u, t);

            if (col != null) {
              collisions[collisionsFE++] = col;

              if (col.t < next.t) {
                next = col;
              }
            }
          }
        }

        // Find new collisions for b
        if (b != null) {
          col = b.collision(t);

          if (col != null) {
            collisions[collisionsFE++] = col;

            if (col.t < next.t) {
              next = col;
            }
          }

          for (i = 0; i < unitsFE; ++i) {
            u = units[i];

            if (b.id != u.id && b.can(u)) {
              col = b.collision(u, t);

              if (col != null) {
                collisions[collisionsFE++] = col;

                if (col.t < next.t) {
                  next = col;
                }
              }
            }
          }
        }
      }
    }
  }

  private static boolean mustErase(Collision col, Unit a, Unit b) {
    if (a.id == col.a.id) {
      return true;
    }

    if (b != null && col.b != null) {
      if (a.id == col.b.id
        || b.id == col.a.id
        || b.id == col.b.id) {
        return true;
      }
    } else if (b != null) {
      if (b.id == col.a.id) {
        return true;
      }
    } else if (col.b != null) {
      if (a.id == col.a.id) {
        return true;
      }
    }
    return false;
  }

  private static void dummies() {
    if (hisWizard1.snaffle != null) {
      hisWizard1.snaffle.thrust(500.0, hisGoal.x, hisGoal.y, hisWizard1.position.distTo(hisGoal));
    } else {
      Snaffle target = null;
      double targetD = Double.MAX_VALUE;
      double d;

      for (int i = 0; i < snafflesFE; ++i) {
        Snaffle snaffle = snaffles[i];

        if (!snaffle.dead) {
          d = hisWizard1.position.squareDistance(snaffle.position);

          if (d < targetD) {
            targetD = d;
            target = snaffle;
          }
        }
      }

      if (target != null) {
        hisWizard1.thrust(150.0, target.position.x, target.position.y, Math.sqrt(targetD));
      }
    }

    if (hisWizard2.snaffle != null) {
      hisWizard2.snaffle.thrust(500.0, hisGoal.x, hisGoal.y, hisWizard2.position.squareDistance(hisGoal));
    } else {
      Snaffle target = null;
      double targetD = Double.MAX_VALUE;
      double d;

      for (int i = 0; i < snafflesFE; ++i) {
        Snaffle snaffle = snaffles[i];

        if (!snaffle.dead) {
          d = hisWizard2.position.squareDistance(snaffle.position);

          if (d < targetD) {
            targetD = d;
            target = snaffle;
          }
        }
      }

      if (target != null) {
        hisWizard2.thrust(150.0, target.position.x, target.position.y, Math.sqrt(targetD));
      }
    }
  }

  private static void simulate(AGSolution solution) {
    energy = 0;
    depth = 0;

    myWizard1.apply(solution, 0, 1);
    myWizard2.apply(solution, 0, 2);
    dummies();

    play();
    depth = 1;

    solution.energy = eval() * 0.1;

    for (int i = 1; i < DEPTH; ++i) {
      myWizard1.apply(solution, i, 1);
      myWizard2.apply(solution, i, 2);
      dummies();

      play();
      depth += 1;
    }

    solution.energy += energy + eval();

    reset();
  }

  private static double eval() {
    //TODO todo :)
    double sqdist = 0;
    for (int i=0;i<snafflesFE;i++) {
       sqdist+= snaffles[i].position.squareDistance(myGoal) / snafflesFE;
    }
    double distToGoal = (16000*16000+7500*7500)-sqdist;
    
    double distToWizards = 0; //myWizard1.position.squareDistance(myWizard2.position);
    
    return distToGoal + distToWizards;
  }

  private static void updateSnaffleSpells() {
    // Snaffles pour tous les sorts sauf obliviate
    for (int i = 1; i < 4; ++i) {
      for (int j = 0; j < snafflesFE; ++j) {
        spellTargets[i][spellTargetsFE[i]++] = snaffles[j];
      }
    }
  }

  private static void updatePetrificus() {
    // Wizards ennemis pour petrificus et flipendo
    if (myTeam == 0) {
      spellTargets[Spell.PETRIFICUS][spellTargetsFE[Spell.PETRIFICUS]++] = wizards[2];
      spellTargets[Spell.PETRIFICUS][spellTargetsFE[Spell.PETRIFICUS]++] = wizards[3];
      spellTargets[Spell.FLIPENDO][spellTargetsFE[Spell.FLIPENDO]++] = wizards[2];
      spellTargets[Spell.FLIPENDO][spellTargetsFE[Spell.FLIPENDO]++] = wizards[3];
    } else {
      spellTargets[Spell.PETRIFICUS][spellTargetsFE[Spell.PETRIFICUS]++] = wizards[0];
      spellTargets[Spell.PETRIFICUS][spellTargetsFE[Spell.PETRIFICUS]++] = wizards[1];
      spellTargets[Spell.FLIPENDO][spellTargetsFE[Spell.FLIPENDO]++] = wizards[0];
      spellTargets[Spell.FLIPENDO][spellTargetsFE[Spell.FLIPENDO]++] = wizards[1];
    }
  }

  private static void updateBludgersSpells() {
    // Bludgers pour tous les sorts
    for (int i = 0; i < 4; ++i) {
      spellTargets[i][0] = bludgers[0];
      spellTargets[i][1] = bludgers[1];
      spellTargetsFE[i] = 2;
    }    
  }

  private static void updateScore() {
    // Mise à jour du score
    if (turn != 0 && oldSnafflesFE != snafflesFE) {
      for (int i = 0; i < 24; ++i) {
        Unit u = unitsById[i];

        if (u != null && u.type == EntityType.SNAFFLE && u.dead) {
          if (myTeam == 0) {
            if (u.position.x > 8000) {
              myScore += 1;
            } else {
              hisScore += 1;
            }
          } else {
            if (u.position.x > 8000) {
              hisScore += 1;
            } else {
              myScore += 1;
            }
          }
          System.err.println("Killing unit "+i);
          unitsById[i] = null;
        }
      }
    }    
  }

  private static void createWizards() {
    wizards[0] = new Wizard(0);
    wizards[1] = new Wizard(0);
    wizards[2] = new Wizard(1);
    wizards[3] = new Wizard(1);
    units [0] = wizards[0];
    units[1] = wizards[1];
    units[2] = wizards[2];
    units[3] = wizards[3];
    initTeams();
    mid = new Point(8000, 3750);
  }

  private static void createBludgers() {
    bludgers [0] = new Bludger();
    bludgers[1] = new Bludger();
    units[4] = bludgers[0];
    units[5] = bludgers[1];
  }

  private static void createPoles() {
    poles[0] = new Pole(20, 0, 1750);
    poles[1] = new Pole(21, 0, 5750);
    poles[2] = new Pole(22, 16000, 1750);
    poles[3] = new Pole(23, 16000, 5750);
    units[6] = poles[0];
    units[7] = poles[1];
    units[8] = poles[2];
    units[9] = poles[3];
  }

  private static void initTeams() {
    if (myTeam == 0) {
      myWizard1 = wizards[0];
      myWizard2 = wizards[1];
      hisWizard1 = wizards[2];
      hisWizard2 = wizards[3];
      myGoal = new Point(16000, 3750);
      hisGoal = new Point(0, 3750);
    } else {
      myWizard1 = wizards[2];
      myWizard2 = wizards[3];
      hisWizard1 = wizards[0];
      hisWizard2 = wizards[1];
      myGoal = new Point(0, 3750);
      hisGoal = new Point(16000, 3750);
    }
  }

  private static void initCollisionsCache() {
    collisionsCache = new Collision[COLLISION_SIZE];
    collisions      = new Collision[COLLISION_SIZE];
    tempCollisions  = new Collision[COLLISION_SIZE];
    for (int i = 0; i < COLLISION_SIZE; ++i) {
      collisionsCache[i] = new Collision();
    }
  }

  private static void initConstants() {
    for (int i=0;i<DEPTH;++i) {
      patiences[i] = Math.pow(COEF_PATIENCE, i);
    }
    for (int i = 0; i < ANGLES_LENGTH; ++i) {
      cosAngles[i] = Math.cos(ANGLES[i] * TO_RAD);
      sinAngles[i] = Math.sin(ANGLES[i] * TO_RAD);
    }
  }

}
