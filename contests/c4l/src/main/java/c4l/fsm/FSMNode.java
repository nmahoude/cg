package c4l.fsm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import c4l.GameState;
import c4l.entities.Module;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.molecule.MoleculeComboInfo;
import c4l.molecule.MoleculeInfo;
import c4l.molecule.MoleculeOptimizerNode;

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
   * @return
   */
  public List<Integer> getCompletableSamples(int[] availables) {
    List<Integer> samples = new ArrayList<>();

    MoleculeOptimizerNode node = new MoleculeOptimizerNode();
    node.start(state.ply, availables, me);
    MoleculeComboInfo bestChild = node.getBestChild();
    for (MoleculeInfo info : bestChild.infos) {
      samples.add(info.sampleId);
    }
    
    return samples;
  }

  List<Sample> findDoableSampleInCloud() {
    List<Sample> samples = new ArrayList<>();
    for (Sample sample : state.availableSamples) {
      // before checking if sample is doable, filter on sample health points
      if (me.canCompleteSampleWithMoleculePool(state, sample)) {
        samples.add(sample);
      }
    }
    return samples;
  }
  
  /**
   * Get new sample from SAMPLE or DIAG
   * @return certified an action will be taken (always go somewhere)
   */
  boolean getNewSamples() {
    List<Sample> samples = findDoableSampleInCloud();
    
    // TODO check for sample qualitay before taking it (rank 1 is really interesting ?)
    if (!samples.isEmpty() ) {
      fsm.goTo(Module.DIAGNOSIS, "Found a doable sample @ DIAG");
      return true;
    } else {
      fsm.goTo(Module.SAMPLES, "Nohing in DIAG, go to sample");
      return true;
    }
    
  }
}
