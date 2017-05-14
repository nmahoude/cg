package c4l.fsm;

import java.util.List;

import c4l.entities.Module;
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
      fsm.goTo(Module.MOLECULES);
    }
  }
  
  @Override
  public Module module() {
    return Module.LABORATORY;
  }

}
