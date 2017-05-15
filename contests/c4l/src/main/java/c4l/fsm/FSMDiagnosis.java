package c4l.fsm;

import java.util.List;

import c4l.entities.Module;
import c4l.entities.Sample;
import c4l.molecule.MoleculeComboInfo;
import c4l.molecule.MoleculeInfo;

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
      System.err.println("No more sample, handle it");
      handleCarriedSampleEmpty();
      return;
    }
    
    if (checkIfIHaveEnoughPointsToWin()) {
      fsm.goTo(Module.LABORATORY, "Afer taking a samples, i have enough points to win already !");
      return;
    }
    
    Sample firstUnDiscoveredSample = getFirstUnDiscoveredSample();
    if (firstUnDiscoveredSample != null) {
      fsm.connect(firstUnDiscoveredSample.id, "Got an undiscover sample, diag it");
      return;
    }      
    
    // TODO check if we can exchange a sample to get better result (2 turns taken)
    
    // check if we can complete a sample (that we just get) from availables molecules in the pool
    MoleculeComboInfo combo = fsm.getBestComboForSamples();
    
    if (!combo.canFinishAtLeastOneSample()) {
      System.err.println("no molecule combination for actual samples, get a new one ?");
     
      if (me.carriedSamples.size() < 3 ) {  
        List<Sample> samples = findDoableSampleInCloud();
        
        if (!samples.isEmpty()) {
          //TODO don't get the first one ! there is better way to choose (xp, min health to win)
          fsm.connect(samples.get(0).id, "Found a sample in the cloud ! Happy :)");
          return;
        }
      }
      
      if (me.carriedSamples.size() > 0) {
        //TODO score the sample difficulty, health and completion before ditching it
        me.carriedSamples.sort(Sample.roiASC());
        fsm.connect(me.carriedSamples.get(0).id, "Ditch a sample in the hope of getting one that fit (roi not enough)");
        return;
      } else {
        fsm.goTo(Module.SAMPLES, "Go to Sample to get a new sample, no combination possible even in the cloud");
        return;
      }
    }

    // before getting to the MOLECULES, check if we need more samples
    if (me.carriedSamples.size() < 3 ) {
      if (me.potentialScore() < 170) {
        List<Sample> samples = findDoableSampleInCloud();
        samples.sort(Sample.orderByHealthDecr);
        // TODO maybe the better health is not the better choice (enough health to win, easier to get ...)
        // TODO introduce the concept of ROI ?
        if (!samples.isEmpty()) {
          fsm.connect(samples.get(0).id, "get another sample while at DIAG");
          return;
        }
      }
    }
    
    if (combo.neededMoleculeToRealiseCombo() == 0) {
      fsm.goTo(Module.LABORATORY, "filled a sample, but no molecule needed and no more sample can be fullfilled");
      return;
    } else {
      fsm.goTo(Module.MOLECULES, "Got at least one way to do sample ");
      return;
    }
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
