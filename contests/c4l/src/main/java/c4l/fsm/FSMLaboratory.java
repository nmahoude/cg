package c4l.fsm;

import java.util.List;

import c4l.entities.Module;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.molecule.MoleculeComboInfo;
import c4l.sample.SampleOptimizer;

public class FSMLaboratory extends FSMNode {
  FSMLaboratory(FSM fsm) {
    super(fsm);
  }
  @Override
  public void think() {
    // TODO Maybe don't put all samples in LAB if we block the opponent !
     MoleculeComboInfo completableSamplesInfo = getCompletableSamples(new int[] {0, 0, 0, 0, 0});
     List<Integer> completableSamples = getCompletableSampleIds(completableSamplesInfo);
     
     if (checkIfHeHisBlockedIfIDontCompleteSamples(completableSamplesInfo)) {
       return;
     }
     
    if (!completableSamples.isEmpty()) {
      fsm.connect(completableSamples.get(0), "Got a full sample in the bag");
      return;
    } else {
      MoleculeComboInfo combo = fsm.getBestComboForSamples();
      if (combo.canFinishAtLeastOneSample()) {
        fsm.goTo(Module.MOLECULES, " go back to MOLECULES, i can pick some");
        return;
      } else if (me.carriedSamples.size() == 3) {
        fsm.goTo(Module.DIAGNOSIS, "go to diag, I have already 3 samples");
        return;
      } else {
        SampleOptimizer optimizer = new SampleOptimizer();
        List<Sample> bestSamples = optimizer.optimize(state, me);
        if (bestSamples.size() >= 2) {
          fsm.goTo(Module.DIAGNOSIS, "Found samples in the cloud, go get them");
          return;
        } else {
          fsm.goTo(Module.SAMPLES, "Not enough samples in the cloud, go to SAMPLE "+bestSamples.toString());
          return;
        }
      }
    }
  }
  
  /**
   *  Maybe don't put all samples in LAB if we block the opponent !
   *  And our score +anticipation is better
   * @return
   */
  private boolean checkIfHeHisBlockedIfIDontCompleteSamples(MoleculeComboInfo completableSamplesInfo) {
    Robot him = state.robots[1];

    if (him.target != Module.MOLECULES) return false;
    if (completableSamplesInfo.infos.isEmpty()) return false;
    
    for (Sample sample : him.carriedSamples) {
      if (him.canCompleteSampleWithMoleculePool(state, sample)) return false;
    }
    // ok we blocked him, now is it interesting ??
    int potentialScore = 0;
    for (int i=0;i<completableSamplesInfo.infos.size();i++) {
      if (state.ply + (i+1) > 200) continue; // no time to complete
      potentialScore += completableSamplesInfo.infos.get(i).points;
    }
    if (me.score +potentialScore < him.score) return false; // don't block if we will be behind
    
    fsm.goTo(Module.LABORATORY, "Stay at LAB, blocking him");
    // DEBUG sauvage:) System.out.println("CAS DE BLOCAGE ! ");
    return true;
  }
  
  @Override
  public Module module() {
    return Module.LABORATORY;
  }

}
