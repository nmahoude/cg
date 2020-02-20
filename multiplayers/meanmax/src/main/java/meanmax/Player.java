package meanmax;

import java.util.Scanner;

import meanmax.entities.Destroyer;
import meanmax.entities.Doof;
import meanmax.entities.Reaper;

public class Player {
  public static double[] rages = { 30, 60, 30};
  public int index;
  public Reaper reaper = new Reaper();
  public Destroyer destroyer = new Destroyer();
  public Doof doof = new Doof();
  
  public double rage = 0;
  public double score = 0;
  
  private double b_rage;
  private double b_score;
  
  public void backup()  {
    b_rage = rage;
    b_score = score;
  }
  
  public void restore() {
    rage = b_rage;
    score = b_score;
  }
  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    Game.play(in);
  }

  public double deltaScore() {
    return score - b_score;
  }

  public double deltaRage() {
    return rage-b_rage;
  }
}