package c4l.fsm;

import java.util.List;

import c4l.entities.Module;
import c4l.entities.MoleculeType;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.molecule.MoleculeOptimizerNode;

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
      handleCarriedSampleEmpty();
    }
    
    MoleculeType type = fsm.getBestMoleculeForSamples();
    if (type != null) {
      fsm.connect(type.toString());
      return;
    }

    List<Sample> completableSamples = getCompletableSamples();
    if (!completableSamples.isEmpty()) {
      fsm.goTo(Module.LABORATORY);
      return;
    }
    
    System.err.println("Can't complete any sample, need to do something ...");
    if (me.carriedSamples.size() == Robot.MAX_SAMPLES) {
      // TODO check if we can wait at the molecule ?
      fsm.goTo(Module.DIAGNOSIS);
    } else  {
      System.err.println("I have room for a new sample, decide DIAG or SAMPLE");
      List<Sample> samples = findDoableSampleInCloud();
      if (samples.isEmpty()) {
        System.err.println("Found nothing in the cloud, go to samples");
        fsm.goTo(Module.SAMPLES);
        return;
      } else {
        System.err.println("Found something in the cloud, go get it");
        fsm.goTo(Module.DIAGNOSIS);
        return;
      }
    }
  }
}
