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
    
    List<Sample> completableSamples = getCompletableSamples();
    if (!completableSamples.isEmpty()) {
      fsm.goTo(Module.LABORATORY);
      return;
    }

    MoleculeType type = fsm.getBestMoleculeForSamples();
    if (type != null) {
      fsm.connect(type.toString());
    } else {
      fsm.goTo(Module.DIAGNOSIS);
    }
  }
}
