package c4l.fsm;

import java.util.List;

import c4l.GameState;
import c4l.Order;
import c4l.entities.Module;
import c4l.entities.MoleculeType;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.molecule.MoleculeComboInfo;
import c4l.molecule.MoleculeInfo;

public class FSMMolecule extends FSMNode {

  FSMMolecule(FSM fsm) {
    super(fsm);
  }

  @Override
  public Module module() {
    return Module.MOLECULES;
  }
  
  @Override
  public void think() {
    if (me.carriedSamples.isEmpty()) {
      getNewSamples();
      return;
    }
    
    if (checkIfIHaveEnoughPointsToWin()) {
      fsm.goTo(Module.LABORATORY, "I have enough points to win, go @LAB");
      return;
    }
    
    MoleculeComboInfo combo = fsm.getBestComboForSamples();
    
    if (getANeededMoleculeForCombo(combo)) {
      return;
    }
    
    // TODO here we don't have any combo left, but maybe we can do something to block HIM !!! or help us later
    if (false) {
      // fill with anything
      if (me.getTotalCarried() < 10) {
        for (MoleculeType type : MoleculeType.values()) {
            if (state.availables[type.index] > 0) {
                fsm.connect(type, "Get the 1st molecule I found, to fill me, I'm so hungry");
                return;
            }
        }
      }
    }
    if (checkToBlockHim()) {
      return;
    }

    List<Sample> completableSamples = getCompletableSamples();
    if (!completableSamples.isEmpty()) {
      fsm.goTo(Module.LABORATORY, "Go to laboratory to get new samples, found some I can do");
      return;
    }
    
    System.err.println("Can't complete any sample, need to do something ...");
    if (me.carriedSamples.size() == Robot.MAX_SAMPLES) {
      // TODO check if we can wait at the molecule ?
      fsm.goTo(Module.DIAGNOSIS, "Can't complete sample & max samples -> @DIAG");
    } else  {
      System.err.println("I have room for a new sample, decide DIAG or SAMPLE");
      List<Sample> samples = findDoableSampleInCloud();
      if (samples.isEmpty()) {
        fsm.goTo(Module.SAMPLES, "Found nothing in the cloud, go to samples");
        return;
      } else {
        fsm.goTo(Module.DIAGNOSIS, "Found something in the cloud, go get it");
        return;
      }
    }
  }

  /**
   * Check if we can get a molecule to complete (partially one of our samples)
   * @param combo
   * @return
   */
  private boolean getANeededMoleculeForCombo(MoleculeComboInfo combo) {
    for (MoleculeInfo info : combo.infos) {
      if (!info.getNeededMolecules().isEmpty()) {

        // Aggressive behavior, try take molecule that can block him first
        MoleculeType bestType = null;
        bestType = findTheBestBlockingMoleculeFor(info.getNeededMolecules(), state.robots[1]);
        if (bestType != null) {
          fsm.connect(bestType, "Get a needed molecule which blocks him");
          return true;
        }

        // hmmm so we couldn't find a molecule that blocks him, find what could block me
        bestType = findTheBestBlockingMoleculeFor(info.getNeededMolecules(), me);
        if (bestType != null) {
          fsm.connect(bestType, "Get a most needed molecule for me");
          return true;
        }
        
        bestType = null;
        int diff = Integer.MAX_VALUE;
        for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
          int delta = state.availables[i] - info.moleculesNeeded[i];
          if (delta < diff) {
            diff = delta;
            bestType= MoleculeType.values()[i];
          } else if (delta == diff) {
            // equality -> take the one the closer to 0 availability;
            if (state.availables[bestType.index] > state.availables[i]) {
              bestType = MoleculeType.values()[i];
            }
          }
        }
        fsm.connect(info.getNeededMolecules().get(0), "Get the best molecule");
        return true;
      } else {
        // don't need molecule for this one, try another one
      }
    }
    return false;
  }

  private MoleculeType findTheBestBlockingMoleculeFor(List<MoleculeType> allowedMolecules, Robot robot) {
    int potentialBlockedSamples[] = getPotentialBlockedSampleByMolecule(state.availables, state.robots[1].carriedSamples, state.robots[1]); 
    MoleculeType bestType = null;
    for (MoleculeType type : allowedMolecules) {
      if (bestType == null || potentialBlockedSamples[type.index] > potentialBlockedSamples[bestType.index] ) {
        bestType = type;
      }
    }
    return bestType; 
  }

  private boolean checkToBlockHim() {
    if (me.getTotalCarried() == 10) {
      return false;
    }
    state.robots[1].carriedSamples.sort(Sample.roiSorter(Order.DESC));
    
    int moleculeToBlock[] = getPotentialBlockedSampleByMolecule(state.availables, state.robots[1].carriedSamples, state.robots[1]);
    
    int best = 0;
    for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
      if (moleculeToBlock[i] > moleculeToBlock[best]) {
        best = i;
      }
    }
    if (moleculeToBlock[best] > 0) {
      fsm.connect(MoleculeType.values()[best], "Ah ah ! I can block him for "+moleculeToBlock[best]+" samples by taking "+MoleculeType.values()[best]+" , so do it");
      return true;
    }
    return false;
  }
  
  /**
   * Return an array with how many sample we can block if we take the molecule at each array index
   */
  private int[] getPotentialBlockedSampleByMolecule(int[] availables, List<Sample> samples, Robot robot) {
    int moleculeToBlock[] = new int[GameState.MOLECULE_TYPE];

    // for each sample, calculate what would block the opp
    for (Sample sample : robot.carriedSamples) {
      for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
        if (availables[i]>0 && availables[i] == sample.costs[i] - (robot.expertise[i]+robot.storage[i])) {
          moleculeToBlock[i]++;
        }
      }
    }
    return moleculeToBlock;
  }
}
