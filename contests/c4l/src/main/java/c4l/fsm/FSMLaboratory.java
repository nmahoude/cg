package c4l.fsm;

import java.util.List;

import c4l.entities.Module;
import c4l.entities.Sample;
import c4l.molecule.MoleculeComboInfo;

public class FSMLaboratory extends FSMNode {
  FSMLaboratory(FSM fsm) {
    super(fsm);
  }
  @Override
  public void think() {
    // TODO Maybe don't put all samples in LAB if we block the opponent !
    List<Sample> completableSamples = getCompletableSamples();
    completableSamples.sort(Sample.orderByHealthDecr); //TODO what about science project ...
    
    if (!completableSamples.isEmpty()) {
      completableSamples.sort(Sample.pointsWonDESC(state, me));
      fsm.connect(completableSamples.get(0).id, "Got a full sample in the bag");
    } else {
      if (me.carriedSamples.isEmpty()) {
        getNewSamples();
        return;
      }
      MoleculeComboInfo combo = fsm.getBestComboForSamples();
      if (combo.canFinishAtLeastOneSample()) {
        fsm.goTo(Module.MOLECULES, " go back to MOLECULES, i can pick some");
        return;
      } else if (me.carriedSamples.size() == 3) {
        fsm.goTo(Module.DIAGNOSIS, "go to diag, I have already 3 samples");
        return;
      } else {
        List<Sample> samples = findDoableSampleInCloud();
        if (samples.isEmpty()) {
          fsm.goTo(Module.SAMPLES, "No doable samples in the cloud, go to SAMPLE");
          return;
        } else {
          fsm.goTo(Module.DIAGNOSIS, "Found samples in the cloud, go get them");
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
