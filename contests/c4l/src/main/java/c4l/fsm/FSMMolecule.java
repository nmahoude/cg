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
import c4l.sample.SampleOptimizer;

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
    
    MoleculeComboInfo combo = fsm.getBestComboForSamples();
    
    if (getANeededMoleculeForCombo(combo)) {
      return;
    }
    
    // here we don't have any combo left, but maybe we can do something to block HIM !!! or help us later
    if (checkToBlockHim()) {
      return;
    }

    // TODO here we may have some sample left (no completed) and wish to get some more molecules
    if (me.getTotalCarried() < 10) {
      // TODO greedy to block him ???
      // Exemple : if i can take enough molecule to block his samples (more than one to be effective, i may do it here
      // Further thinking : can i mix the 3 type of greedy to get a score :)
      
      // get the molecule for next samples (the one i can not complete now
      for (Sample sample : me.carriedSamples) {
        if (me.canCompleteSampleAuto(sample))
          continue;
        MoleculeType type = me.getMissingMoleculeForSample(state, sample);
        if (type != null) {
          fsm.connect(type, "Get a molecule for a future sample");
          return;
        }
      }
      
      // get the molecule I may need the most (less xp)
      int minXp = Integer.MAX_VALUE;
      MoleculeType type = null;
      for (int i=0;i<GameState.MOLECULE_TYPE;i++) {
        if (state.availables[i] > 0 && me.expertise[i] < minXp) {
          minXp = me.expertise[i];
          type = MoleculeType.values()[i];
        }
      }
      if (type != null) {
        fsm.connect(type, "Get a molecule I may need the more (less XP)");
        return;
      }
    }
    
    MoleculeComboInfo completableSamplesInfo = getCompletableSamples(new int[] {0, 0, 0, 0, 0});
    List<Integer> completableSamples = getCompletableSampleIds(completableSamplesInfo);
    
    if (!completableSamples.isEmpty()) {
      fsm.goTo(Module.LABORATORY, "Go to laboratory to collect points, found some samples I can complete");
      return;
    }
    
    System.err.println("Can't complete any sample, need to do something ...");
    if (me.carriedSamples.size() == Robot.MAX_SAMPLES) {
      // TODO check if we can wait at the molecule ?
      fsm.goTo(Module.DIAGNOSIS, "Can't complete sample & max samples -> @DIAG");
    } else  {
      System.err.println("I have room for a new sample, decide DIAG or SAMPLE");
      SampleOptimizer optimizer = new SampleOptimizer();
      List<Sample> bestSamples = optimizer.optimize(state, me);
      if (bestSamples.size() >= 2) {
        fsm.goTo(Module.DIAGNOSIS, "Found samples in the cloud, go get them");
        return;
      } else {
        fsm.goTo(Module.SAMPLES, "No enough samples in the cloud, go to SAMPLE "+bestSamples.toString());
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
          fsm.connect(bestType, "BLOCK HIM ! Get a needed molecule which blocks him");
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
    int potentialBlockedSamples[] = getPotentialBlockedSampleByMolecule(state.availables, robot.carriedSamples, robot); 
    MoleculeType bestType = null; 
    int bestScore = 0;// 0 is not a good value, so prohibit it by choosing this best value
    for (MoleculeType type : allowedMolecules) {
      if (potentialBlockedSamples[type.index] > bestScore ) {
        bestScore = potentialBlockedSamples[type.index];
        bestType = type;
      }
    }
    return bestType; 
  }

  private boolean checkToBlockHim() {
    if (me.getTotalCarried() == 10) {
      return false; // won't be able to take a molecule
    }
    
    MoleculeType bestType = findTheBestBlockingMoleculeFor(MoleculeType.all, state.robots[1]);
    if (bestType != null) {
      fsm.connect(bestType, "BLOCK HIM ! We can block him, take some risks and get the molecule" + bestType);
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

  MoleculeType getSparserMolecule(int[] moleculesNeeded) {
    MoleculeType moleculeSparse = null;
    int diff = Integer.MAX_VALUE;
    for (int i = 0; i < GameState.MOLECULE_TYPE; i++) {
      if (moleculesNeeded[i] == 0) continue;
      
      int delta = Math.max(0, state.availables[i] - moleculesNeeded[i]);
      if (delta < diff) {
        diff = delta;
        moleculeSparse = MoleculeType.values()[i];
      } else if (delta == diff) {
        // equality -> take the one the closer to 0 availability;
        if (state.availables[moleculeSparse.index] > state.availables[i]) {
          moleculeSparse = MoleculeType.values()[i];
        }
      }
    }
    return moleculeSparse;
  }
}
