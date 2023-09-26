package oldcvz;

import java.util.Scanner;

public class State {

  public Ash ash = new Ash();

  public Human[] humans = null;

  public Zombie[] zombies = null;

  public int aliveHumans = 0;
  public int aliveZombies= 0;
  
  /** Backups */
  /** Backups */
  Ash b_ash = new Ash();

  Human[] b_humans = null;

  Zombie[] b_zombies = null;

  private int b_aliveHumans;
  int b_aliveZombies;

  /***/
  /***/
  State() {
  }

  public void readSetup() {
  }

  public void read(Scanner in) {
    init();
    ash.p.copyFrom(in.nextInt(), in.nextInt());
    System.err.println(""+(int)ash.p.x+" "+(int)ash.p.y);
    
    int humanCount = in.nextInt();
    System.err.println(""+humanCount);
    if (humans == null) {
      initHumans(humanCount);
    }
    
    aliveHumans = 0;
    for (int i = 0; i < humanCount; i++) {
      int humanId = in.nextInt();
      int humanX = in.nextInt();
      int humanY = in.nextInt();
      System.err.println(""+humanId+" "+humanX+" "+humanY);
      humans[i].update(humanX, humanY);
      aliveHumans++;
    }
    int zombieCount = in.nextInt();
    System.err.println(""+zombieCount);
    if (zombies == null) {
      initZombies(zombieCount);
    }
    aliveZombies = 0;
    for (int i = 0; i < zombieCount; i++) {
      int zombieId = in.nextInt();
      int zombieX = in.nextInt();
      int zombieY = in.nextInt();
      int zombieXNext = in.nextInt();
      int zombieYNext = in.nextInt();
      System.err.println(""+zombieId+" "+zombieX+" "+zombieY+" "+zombieXNext+" "+zombieYNext);

      zombies[i].update(zombieX, zombieY, zombieXNext, zombieYNext);
      aliveZombies++;
    }
    backup();
  }

  private void backup() {
    b_ash.copy(ash);
    for (int i = 0; i < humans.length; i++) {
      b_humans[i].copy(humans[i]);
    }
    for (int i = 0; i < zombies.length; i++) {
      b_zombies[i].copy(zombies[i]);
    }
    b_aliveHumans = aliveHumans;
    b_aliveZombies = aliveZombies;
  }

  public void restore() {
    ash.copy(b_ash);
    for (int i = 0; i < humans.length; i++) {
      humans[i].copy(b_humans[i]);
    }
    for (int i = 0; i < zombies.length; i++) {
      zombies[i].copy(b_zombies[i]);
    }
    aliveHumans = b_aliveHumans;
    aliveZombies = b_aliveZombies;
  }

  private void init() {
    if (humans == null) {
      return;
    }
    for (Human h : humans) {
      h.init();
    }
    for (Zombie z : zombies) {
      z.init();
    }
  }

  private void initZombies(int zombieCount) {
    zombies = new Zombie[zombieCount];
    b_zombies = new Zombie[zombieCount];
    for (int i = 0; i < zombieCount; i++) {
      zombies[i] = new Zombie(i);
      b_zombies[i] = new Zombie(i);
    }
  }

  private void initHumans(int humanCount) {
    humans = new Human[humanCount];
    b_humans = new Human[humanCount];
    for (int i = 0; i < humanCount; i++) {
      humans[i] = new Human(i);
      b_humans[i] = new Human(i);
    }
  }

  public void debugDistances() {
    for (Human h : humans) {
      if (h.dead) continue;
      
      Zombie z = h.getCloserZombie(zombies);
      int aStepsToH = (int)(1.0 * (ash.p.distTo(h.p)) / 1000);
      int zStepsToH;
      if (z != null) {
        zStepsToH = (int)(z.p.distTo(h.p) / 400);
      } else {
        zStepsToH = 1_000_000;
      }
      double score = zStepsToH - aStepsToH;
      System.err.println("H "+h.id+" @"+h.p+" closeZ:"+z.id+" steps Ash : "+aStepsToH+" zSteps: "+zStepsToH);
    }
  }
}