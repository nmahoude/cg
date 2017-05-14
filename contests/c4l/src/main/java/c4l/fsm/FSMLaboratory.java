package c4l.fsm;

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
      } else if (me.carriedSamples.size() < 3) {
        fsm.goTo(Module.SAMPLES);
        return;
      } else {
        fsm.goTo(Module.DIAGNOSIS);
        return;
      }
    }
  }
  
  @Override
  public Module module() {
    return Module.LABORATORY;
  }

}
