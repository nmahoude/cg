package fantasticBitsMulti;

import java.awt.Point;
import java.util.Scanner;

import fantasticBitsMulti.spells.Spell;
import fantasticBitsMulti.units.Bludger;
import fantasticBitsMulti.units.Pole;
import fantasticBitsMulti.units.Snaffle;
import fantasticBitsMulti.units.Unit;
import fantasticBitsMulti.units.Wizard;
import random.FastRand;

public class Player {
  private static final int COLLISION_SIZE = 100;
  static final double TO_RAD = Math.PI / 180.0;
  static final int ANGLES_LENGTH = 36;
  static final double ANGLES[] = new double[]{0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0, 110.0, 120.0, 130.0, 140.0, 150.0, 160.0, 170.0, 180.0, 190.0, 200.0, 210.0, 220.0, 230.0, 240.0, 250.0, 260.0, 270.0, 280.0, 290.0, 300.0, 310.0, 320.0, 330.0, 340.0, 350.0};
  public static final int DEPTH = 4;
  static final double COEF_PATIENCE = 0.9;
  public static final double E = 0.00001;

  public static double cosAngles[] = new double[ANGLES_LENGTH];
  public static double sinAngles[] = new double[ANGLES_LENGTH];
  static double patiences[] = new double[DEPTH];
  
  static FastRand rand;
  public static int myTeam;
  

  static Collision fake;
  public static int collisionsCacheFE = 0;
  public static Collision[] collisionsCache;
  static int collisionsFE = 0;
  static Collision[] collisions;
  static int tempCollisionsFE = 0;
  static Collision[] tempCollisions;
  
  public static Wizard[] wizards = new Wizard[4];
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
  public static int mana;
  public static int myScore;
  public static int hisScore;
  public static Snaffle[] snaffles;
  public static int snafflesFE;

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
      int entities = in.nextInt();
      for (int i = 0; i < entities; i++) {
        int entityId = in.nextInt();
        int entityType = in.nextInt();
        int x = in.nextInt();
        int y = in.nextInt();
        int vx = in.nextInt();
        int vy = in.nextInt();
        int state = in.nextInt();
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
