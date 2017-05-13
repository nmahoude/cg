package c4l.entities;

import java.util.Scanner;

import c4l.GameState;

public class ScienceProject {
  public int expertiseNeeded[] = new int [GameState.MOLECULE_TYPE];
  
  public void read(Scanner in) {
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      expertiseNeeded[i] = in.nextInt();
    }
  }

}
