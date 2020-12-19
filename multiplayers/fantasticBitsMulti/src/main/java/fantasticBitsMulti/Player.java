package fantasticBitsMulti;

import java.util.Scanner;

import fantasticBitsMulti.ag.AG;
import fantasticBitsMulti.ag.AGSolution;
import fantasticBitsMulti.simulation.Simulation;
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
  public static final long TIME_LIMIT = 85;

  static final double TO_RAD = Math.PI / 180.0;
  public static final int ANGLES_LENGTH = 36;
  static final double ANGLES[] = new double[]{0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0, 110.0, 120.0, 130.0, 140.0, 150.0, 160.0, 170.0, 180.0, 190.0, 200.0, 210.0, 220.0, 230.0, 240.0, 250.0, 260.0, 270.0, 280.0, 290.0, 300.0, 310.0, 320.0, 330.0, 340.0, 350.0};
  public static final double E = 0.001;
  public static double cosAngles[] = new double[ANGLES_LENGTH];
  public static double sinAngles[] = new double[ANGLES_LENGTH];
  
  public static FastRand rand;
  public static int myTeam;
  

  public static Wizard[] wizards = new Wizard[4];
  static Unit unitsById[] = new Unit[24];
  public static int unitsFE = 0;
  public static Unit[] units = new Unit[20];
  public static Wizard myWizard1;
  public static Wizard myWizard2;
  public  static Wizard hisWizard1;
  public static Wizard hisWizard2;
  public static Point myGoal;
  public static Point hisGoal;
  private static Point mid;
  public static Bludger[] bludgers = new Bludger[2];
  private static Pole[] poles = new Pole[4];
  
  public static Spell spells[] = new Spell[4*4];
  public static Unit spellTargets[][] = new Unit[4][20];
  public static int spellTargetsFE[] = new int[4];

  public static int myMana;
  public static int myScore;
  public static int hisScore;
  private static int hisMana;
  
  
  public static int _myMana;
  public static int _myScore;
  public static int _hisScore;
  private static int _hisMana;
  
  
  
  public static Snaffle[] snaffles = new Snaffle[10];
  public static int snafflesFE;
  public static long start;
  public static int turn = 0;
  public static int victory;
  private static int bludgersFE;
  
  
  static {
    initConstants();
    rand = new FastRand(73);
  }
  
  public static void backupState() {
    _myMana = myMana;
    _myScore = myScore;
    _hisMana = hisMana;
    _hisScore = hisScore;
    
    for (int i = 0; i < unitsFE; ++i) {
      units[i].save();
    }

    for (int i = 0; i < 16; ++i) {
      spells[i].save();
    }
  }
  
  public static void restoreState() {
    myMana = _myMana;
    myScore = _myScore;
    hisMana = _hisMana;
    hisScore = _hisScore;
    
    for (int i = 0; i < unitsFE; ++i) {
      units[i].reset();
    }

    for (int i = 0; i < 16; ++i) {
      spells[i].reset();
    }
  }
  
  static public void init(int myTeam) {
    Player.myTeam = myTeam;
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
  }
  
  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);

    init(in.nextInt());
    
    while (true) {
      bludgersFE = 0;
      snafflesFE = 0;
      resetSnaffles();

      myScore = in.nextInt();
      updateStartAfter1stRead();
      myMana = in.nextInt();
      hisScore = in.nextInt();
      hisMana = in.nextInt();
      
      TestOutputer.output(myScore, myMana, hisScore, hisMana);
      
      int entities = in.nextInt();
      for (int i = 0; i < entities; i++) {
        int id = in.nextInt();
        String entity = in.next();
        EntityType entityType = EntityType.valueOf(entity);
        int x = in.nextInt();
        int y = in.nextInt();
        int vx = in.nextInt();
        int vy = in.nextInt();
        int state = in.nextInt();
        
        // System.err.println("createUnit("+id+", \""+entity+"\", "+x+", "+y+", "+vx+", "+vy+", "+state+");");
        TestOutputer.output(id, entity, x, y, vx, vy, state);
        Unit unit = null;
        if (entityType == EntityType.WIZARD || entityType == EntityType.OPPONENT_WIZARD)  {
          unit = wizards[id];
        } else if (entityType == EntityType.SNAFFLE) {
          if (turn == 0) {
            unit = new Snaffle();
          } else {
            unit = unitsById[id];
          }
          units[unitsFE++] = unit;
          snaffles[snafflesFE++] = (Snaffle)unit;
        } else if (entityType == EntityType.BLUDGER) {
          unit = bludgers[bludgersFE++];
        }
        unit.update(id, x, y, vx, vy, state);
      }
      
      if (turn == 0) {
        victory = (snafflesFE / 2 ) + 1;
        affectUnitsToUnitsById();
      }

      // Mise à jour des carriers et des snaffles
      updateWizardsAndSnaffles();
      updateBludgersSpells();
      updatePetrificus();
      updateSnaffleSpells();
      

      for (int i = 0; i < 16; ++i) {
        spells[i].checkTarget();
      }
      
      backupState();
      for (int i=0;i<4;i++) {
        if (Player.wizards[i].snaffle != null) {
          TestOutputer.outputCommand("Player.wizards["+i+"].snaffle"+Player.wizards[i].snaffle.id);
        }
      }
      AGSolution solution = AG.evolution();

      myWizard1.output(solution.moves1[0], solution.spellTurn1, solution.spell1, solution.spellTarget1);
      myWizard2.output(solution.moves2[0], solution.spellTurn2, solution.spell2, solution.spellTarget2);

      Player.turn += 1;
      Player.unitsFE = 10; // 4 poles, 4 wizards & 2 bludgers

    }
  }

  private static void updateStartAfter1stRead() {
    start = System.currentTimeMillis();
    if (turn == 0) {
      start += 800;
    }
  }

  public static void updateWizardsAndSnaffles() {
    for (int i = 0; i < 4; ++i) {
      wizards[i].updateSnaffle();
    }
  }

  public static void affectUnitsToUnitsById() {
    for (int i=0;i<unitsFE;++i) {
      unitsById[units[i].id] = units[i];
    }
  }

  private static void resetSnaffles() {
    for (int i = 0; i < 24; ++i) {
      Unit u = unitsById[i];

      if (u != null && u.type == EntityType.SNAFFLE) {
        u.dead = true;
        u.carrier = null;
      }
    }
  }


  public static void updateSnaffleSpells() {
    // Snaffles pour tous les sorts sauf obliviate
    for (int i = 1; i < 4; ++i) {
      for (int j = 0; j < snafflesFE; ++j) {
        spellTargets[i][spellTargetsFE[i]++] = snaffles[j];
      }
    }
  }

  public static void updatePetrificus() {
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

  public static void updateBludgersSpells() {
    // Bludgers pour tous les sorts
    for (int i = 0; i < 4; ++i) {
      spellTargets[i][0] = bludgers[0];
      spellTargets[i][1] = bludgers[1];
      spellTargetsFE[i] = 2;
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

  private static void initConstants() {
    for (int i = 0; i < ANGLES_LENGTH; ++i) {
      cosAngles[i] = Math.cos(ANGLES[i] * TO_RAD);
      sinAngles[i] = Math.sin(ANGLES[i] * TO_RAD);
    }
  }

}
