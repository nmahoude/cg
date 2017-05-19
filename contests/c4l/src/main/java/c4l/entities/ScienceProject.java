package c4l.entities;

import java.util.Arrays;
import java.util.Scanner;

import c4l.GameState;

public class ScienceProject {
  public int expertiseNeeded[] = new int [GameState.MOLECULE_TYPE];
  public int doneBy = -1;
      
  public ScienceProject() {
  }

  public ScienceProject(int[] is) {
    this();
    expertiseNeeded = is;
  }

  public void read(Scanner in) {
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      expertiseNeeded[i] = in.nextInt();
    }
  }

  public String toString() {
    String output = "createSP("+doneBy+",new int[]{";
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      output += (""+expertiseNeeded[i]);
      if (i != GameState.MOLECULE_TYPE-1) {
        output += (",");
      } else {
        output += ("});");
      }
    }
    return output;
  }
}
