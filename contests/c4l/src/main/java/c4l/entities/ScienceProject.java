package c4l.entities;

import java.util.Arrays;
import java.util.Scanner;

import c4l.GameState;

public class ScienceProject {
  public int expertiseNeeded[] = new int [GameState.MOLECULE_TYPE];
  public int doneBy = -1;
      
  public void read(Scanner in) {
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      expertiseNeeded[i] = in.nextInt();
    }
  }

  @Override
  public String toString() {
    return "Science project : ("+doneBy+")"+Arrays.toString(expertiseNeeded);
  }
}
