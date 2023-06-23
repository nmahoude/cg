package oldcvz;

import java.util.Scanner;

public class GameState {

  public Ash ash = new Ash();

  public Human[] humans = null;

  public Zombie[] zombies = null;

  /** Backups */
  /** Backups */
  Ash b_ash = new Ash();

  Human[] b_humans = null;

  Zombie[] b_zombies = null;

  /***/
  /***/
  GameState() {
  }

  public void readSetup() {
  }

  public void read(Scanner in) {
    init();
    ash.p = new Point(in.nextInt(), in.nextInt());
    int humanCount = in.nextInt();
    if (humans == null) {
      initHumans(humanCount);
    }
    for (int i = 0; i < humanCount; i++) {
      int humanId = in.nextInt();
      int humanX = in.nextInt();
      int humanY = in.nextInt();
      humans[humanId].update(humanX, humanY);
    }
    int zombieCount = in.nextInt();
    if (zombies == null) {
      initZombies(zombieCount);
    }
    for (int i = 0; i < zombieCount; i++) {
      int zombieId = in.nextInt();
      int zombieX = in.nextInt();
      int zombieY = in.nextInt();
      int zombieXNext = in.nextInt();
      int zombieYNext = in.nextInt();
      zombies[zombieId].update(zombieX, zombieY, zombieXNext, zombieYNext);
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
  }

  public void restore() {
    ash.copy(b_ash);
    for (int i = 0; i < humans.length; i++) {
      humans[i].copy(b_humans[i]);
    }
    for (int i = 0; i < zombies.length; i++) {
      zombies[i].copy(b_zombies[i]);
    }
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
}