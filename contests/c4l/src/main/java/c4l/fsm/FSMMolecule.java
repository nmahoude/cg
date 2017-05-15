package c4l.fsm;

import java.util.List;

import c4l.entities.Module;
import c4l.entities.MoleculeType;
import c4l.entities.Robot;
import c4l.entities.Sample;
import c4l.molecule.MoleculeComboInfo;
import c4l.molecule.MoleculeInfo;

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
      return;
    }
    
    if (checkIfIHaveEnoughPointsToWin()) {
      fsm.goTo(Module.LABORATORY, "I have enough points to win, go @LAB");
      return;
    }
    
    MoleculeComboInfo combo = fsm.getBestComboForSamples();
    for (MoleculeInfo info : combo.infos) {
      if (!info.getNeededMolecules().isEmpty()) {
        //TODO here we have the choice of molecule order !
        // take in the order ? default, booouuhh
        // take the famine molecule first (good choice for me)
        // take the one that will block him
        fsm.connect(info.getNeededMolecules().get(0).toString(), "Get the best molecule");
        return;
      } else {
        // don't need molecule for this one
      }
    }
    
    // TODO here we don't have any combo left, but maybe we can do something to block HIM !!!
    if (me.getTotalCarried() < 10) {
      for (MoleculeType type : MoleculeType.values()) {
        if (state.availables[type.index] > 0) {
          fsm.connect(type.toString(), "Get the 1st molecule I found, to fill me, I'm so hungry");
          return;
        }
      }
    }

    List<Sample> completableSamples = getCompletableSamples();
    if (!completableSamples.isEmpty()) {
      fsm.goTo(Module.LABORATORY, "Go to laboratory to get new samples, found some I can do");
      return;
    }
    
    System.err.println("Can't complete any sample, need to do something ...");
    if (me.carriedSamples.size() == Robot.MAX_SAMPLES) {
      // TODO check if we can wait at the molecule ?
      fsm.goTo(Module.DIAGNOSIS, "Can't complete sample & max samples -> @DIAG");
    } else  {
      System.err.println("I have room for a new sample, decide DIAG or SAMPLE");
      List<Sample> samples = findDoableSampleInCloud();
      if (samples.isEmpty()) {
        fsm.goTo(Module.SAMPLES, "Found nothing in the cloud, go to samples");
        return;
      } else {
        fsm.goTo(Module.DIAGNOSIS, "Found something in the cloud, go get it");
        return;
      }
    }
  }
}
