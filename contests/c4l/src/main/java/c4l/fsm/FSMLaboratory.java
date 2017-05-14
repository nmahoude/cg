package c4l.fsm;

import java.util.Comparator;
import java.util.List;

import c4l.entities.Module;
import c4l.entities.MoleculeType;
import c4l.entities.Sample;

public class FSMLaboratory extends FSMNode {
  FSMLaboratory(FSM fsm) {
    super(fsm);
  }
  @Override
  public void think() {
    List<Sample> completableSamples = getCompletableSamples();
    completableSamples.sort(Sample.orderByHealthDecr);
    
    if (!completableSamples.isEmpty()) {
      fsm.connect(completableSamples.get(0).id);
    } else {
      if (me.carriedSamples.isEmpty()) {
        handleCarriedSampleEmpty();
        return;
      }
      MoleculeType type = fsm.getBestMoleculeForSamples();
      if (type != null) {
        fsm.goTo(Module.MOLECULES);
        return;
      } else if (me.carriedSamples.size() == 3) {
        fsm.goTo(Module.DIAGNOSIS);
        return;
      } else {
        List<Sample> samples = findDoableSampleInCloud();
        if (samples.isEmpty()) {
          System.err.println("No doable samples in the cloud, go to SAMPLE");
          fsm.goTo(Module.SAMPLES);
          return;
        } else {
          System.err.println("Found samples in the cloud, go get them");
          fsm.goTo(Module.DIAGNOSIS);
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
