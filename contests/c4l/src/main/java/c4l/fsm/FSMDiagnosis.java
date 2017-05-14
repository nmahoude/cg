package c4l.fsm;

import c4l.entities.Module;
import c4l.entities.MoleculeType;
import c4l.entities.Sample;

public class FSMDiagnosis extends FSMNode {
  FSMDiagnosis(FSM fsm) {
    super(fsm);
  }
  @Override
  public Module module() {
    return Module.DIAGNOSIS;
  }

  @Override
  public void think() {
    if (me.carriedSamples.isEmpty()) {
      handleCarriedSampleEmpty();
      return;
    }
    
    Sample firstUnDiscoveredSample = getFirstUnDiscoveredSample();
    if (firstUnDiscoveredSample != null) {
      fsm.connect(firstUnDiscoveredSample.id);
      return;
    }      
    
    // check if we can complete a sample from availables molecules
    MoleculeType type = fsm.getBestMoleculeForSamples();
    if (type == null) {
      // ditch the samples
      fsm.connect(me.carriedSamples.get(0).id);
      return;
    }

    fsm.goTo(Module.MOLECULES);
  }

  private Sample getFirstUnDiscoveredSample() {
    for(Sample sample : me.carriedSamples) {
      if (!sample.isDiscovered()) {
        return sample;
      }
    }
    return null;
  }
}
