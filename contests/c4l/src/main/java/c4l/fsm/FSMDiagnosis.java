package c4l.fsm;

import java.util.List;

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
    
    // check if we can complete a sample from availables molecules
    MoleculeType type = fsm.getBestMoleculeForSamples();
    if (type == null) {
     
      if (me.carriedSamples.size() < 3 ) {  
        System.err.println("no molecule combination for samples");
        List<Sample> samples = findDoableSampleInCloud();
        
        if (!samples.isEmpty()) {
          fsm.connect(samples.get(0).id, "Found a sample in the cloud ! Happy :)");
          return;
        }
      }
      
      if (me.carriedSamples.size() > 0) {
        //TODO score the sample difficulty, health and completion before ditching it
        fsm.connect(me.carriedSamples.get(0).id, "Ditch a sample in the hope of getting one that fit (roi not enough)");
        return;
      } else {
        fsm.goTo(Module.SAMPLES, "Go to Sample to get a new sample");
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
    fsm.goTo(Module.MOLECULES, "Got at least one way to do sample with molecule "+type.toString());
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
