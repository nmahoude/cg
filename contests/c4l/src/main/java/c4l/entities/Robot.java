package c4l.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import c4l.GameState;

public class Robot {
  
  public static final int MAX_MOLECULES = 10;
  public static final int MAX_SAMPLES = 3;
  
  public final int id;
  public Module target;
  public int eta;
  public int score;
  public int totalCarried;
  public int totalExpertise = 0;
  public int storage[] = new int[5];
  public int expertise[] = new int[5];
  public List<Sample> carriedSamples = new ArrayList<>();
  
  public Robot(int id) {
    this.id = id;
    target = Module.START_POS;
  }
  public void read(Scanner in) {
    target = Module.valueOf(in.next());
    eta = in.nextInt();
    score = in.nextInt();

    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      storage[i] = in.nextInt();
      totalCarried+=storage[i];
    }
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      expertise[i] = in.nextInt();
      totalExpertise += expertise[i];
    }    
  }

  public void clearForRound() {
    totalCarried = 0;
    totalExpertise = 0;
    carriedSamples.clear();
  }
  public boolean isStorageEmpty() {
    return storage[0] == 0 && storage[1] == 0 && storage[2] == 0
        && storage[3] == 0 && storage[4] == 0 && storage[5] == 0;
  }

  public MoleculeType getMissingMoleculeForSample(GameState state, Sample sample) {
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      if (expertise[i] + storage[i] < sample.costs[i] && state.availables[i] > 0) {
        return MoleculeType.values()[i];
      }
    }
    return null;
  }

  public int getTotalCarried() {
    return totalCarried;
  }

  /**
   * Can the robot complete the sample without help of MoleculePool
   * @param the sample to check
   * @return true if we have enough
   */
  public boolean canCompleteSampleAuto(Sample sample) {
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      if (expertise[i]+storage[i] < sample.costs[i]) {
        return false;
      }
    }
    return true;
  }

  /**
   * Can the robot complete the sample with the help of the molecule pool (as is)
   * @param state, the current state
   * @param sample, the sample to check
   * @return true if we can complete it
   */
  public boolean canCompleteSampleWithMoleculePool(GameState state, Sample sample) {
    int freeStorage = 10 - getTotalCarried();
    
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      int disposable = expertise[i] + storage[i] + state.availables[i];
      int cost = sample.costs[i];
      if (disposable < cost) {
        return false;
      }
      // remove molecules
      freeStorage-=Math.max(0, cost - expertise[i]-storage[i]);
      if (freeStorage < 0) {
        return false;
      }
    }
    return true;
  }

  /** return the score + the sum of carried samples */
  public int potentialScore() {
    return score + carriedSamples.stream().mapToInt(s -> s.health).sum();
  }

  public String toString() {
    String output = "createRobot("+id+","+target.ordinal()+","+eta+","+score+", new int[]{";
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      output+=""+storage[i];
      if (i < GameState.MOLECULE_TYPE-1) output+=", ";
    }
    output+="}, new int[]{";
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      output+=""+expertise[i];
      if (i < GameState.MOLECULE_TYPE-1) output+=", ";
    }
    output+="});";
    return output;
  }
  

}
