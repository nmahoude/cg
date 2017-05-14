package c4l.fsm;

import java.util.ArrayList;
import java.util.List;

import c4l.GameState;
import c4l.entities.Module;
import c4l.entities.Robot;
import c4l.entities.Sample;

public abstract class FSMNode {

  FSM fsm;
  Robot me;
  GameState state;
  
  FSMNode(FSM fsm) {
    this.fsm = fsm;
    me = fsm.me;
    state = fsm.state;
  }

  public abstract void think();

  public void prethink() {
  }

  public abstract Module module();
  
  /**
   * Return a list of all completeSamples wr molecules & expertise
   * Warning : it doesn't mean we can complete all at once, 
   * completing one will remove molecules from storage
   * @return
   */
  public List<Sample> getCompletableSamples() {
    List<Sample> samples = new ArrayList<>();
    for (Sample sample : me.carriedSamples) {
      if (!sample.isDiscovered()) continue;
      if (me.hasMolecules(sample)) {
        samples.add(sample);
      }
    }
    return samples;
  }

  /**
   * Common method to handle the case where we don"t have any samples
   */
  void handleCarriedSampleEmpty() {
    //TODO check to recycle at 
    fsm.goTo(Module.SAMPLES);
  }

  List<Sample> findDoableSampleInCloud() {
    List<Sample> samples = new ArrayList<>();
    for (Sample sample : state.availableSamples) {
      if (me.isThereEnoughMoleculeForSample(state, sample)) {
        samples.add(sample);
      }
    }
    return samples;
  }

}
