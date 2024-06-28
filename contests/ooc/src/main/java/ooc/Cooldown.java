package ooc;

import java.util.Scanner;

public class Cooldown {
  public static final int MAX_TORPEDO_COOLDOWN = 3;
  public static final int MAX_MINE_COOLDOWN = 4;
  public static final int MAX_SONAR_COOLDOWN = 4;
  public static final int MAX_SILENCE_COOLDOWN = 6;

  
  public static final int TORPEDO = 0;
  public static final int SILENCE = 1;
  public static final int SONAR = 2;
  public static final int MINE = 3;

  public int cooldowns[] = new int[4];
  
  public Cooldown() {
    reset();
  }

  public void reset() {
    cooldowns[TORPEDO] = MAX_TORPEDO_COOLDOWN;
    cooldowns[SONAR] = MAX_SONAR_COOLDOWN;
    cooldowns[SILENCE] = MAX_SILENCE_COOLDOWN;
    cooldowns[MINE] = MAX_MINE_COOLDOWN;
  }

  public void read(Scanner in) {
    cooldowns[TORPEDO] = in.nextInt();
    cooldowns[SONAR] = in.nextInt();
    cooldowns[SILENCE] = in.nextInt();
    cooldowns[MINE] = in.nextInt();
  }

  public int silenceCooldown() {
    return cooldowns[SILENCE];
  }

  public int torpedoCooldown() {
    return cooldowns[TORPEDO];
  }

  public int sonarCooldown() {
    return cooldowns[SONAR];
  }

  public int mineCooldown() {
    return cooldowns[MINE];
  }

  public void copyFrom(Cooldown model) {
    for (int i=0;i<4;i++) {
      this.cooldowns[i] = model.cooldowns[i];
    }
  }

  public void charge(int index) {
    cooldowns[index] = Math.max(0, cooldowns[index]-1);
  }

  public void resetSilence() {
    cooldowns[SILENCE] = MAX_SILENCE_COOLDOWN;    
  }

  public void resetTorpedo() {
    cooldowns[TORPEDO] = MAX_TORPEDO_COOLDOWN;
  }

  public void resetMine() {
    cooldowns[MINE] = MAX_MINE_COOLDOWN;
  }

  public void resetSonar() {
    cooldowns[SONAR] = MAX_SONAR_COOLDOWN;
    
  }

  public void charge(int index, int value) {
    cooldowns[index] = value;
  }

  public int get(int index) {
    return cooldowns[index];
  }

  public void chargeAll() {
    cooldowns[TORPEDO] = 0;
    cooldowns[SONAR] = 0;
    cooldowns[SILENCE] = 0;
    cooldowns[MINE] = 0;
  }

  public boolean allCharged() {
    return cooldowns[TORPEDO] == 0
    && cooldowns[SONAR] == 0
    && cooldowns[SILENCE] == 0
    && cooldowns[MINE] == 0;
  }

}
