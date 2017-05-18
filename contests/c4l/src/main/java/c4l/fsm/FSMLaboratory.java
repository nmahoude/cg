package c4l.fsm;

import java.util.List;

import c4l.Order;
import c4l.entities.Module;
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
    List<Sample> completableSamples = getCompletableSamples(new int[] {0, 0, 0, 0, 0});
    
    if (!completableSamples.isEmpty()) {
      fsm.connect(completableSamples.get(0).id, "Got a full sample in the bag");
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
          fsm.goTo(Module.SAMPLES, "No doable samples in the cloud, go to SAMPLE");
          return;
        }
      }
    }
  }
  
  @Override
  public Module module() {
    return Module.LABORATORY;
  }

}
