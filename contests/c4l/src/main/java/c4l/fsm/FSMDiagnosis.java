package c4l.fsm;

import java.util.ArrayList;
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
    
    Sample firstUnDiscoveredSample = getFirstUnDiscoveredSample();
    if (firstUnDiscoveredSample != null) {
      System.err.println("Got an undiscover sample, diag it");
      fsm.connect(firstUnDiscoveredSample.id);
      return;
    }      
    
    // check if we can complete a sample from availables molecules
    MoleculeType type = fsm.getBestMoleculeForSamples();
    if (type == null) {
     
      if (me.carriedSamples.size() < 3 ) {  
        System.err.println("no molecule combination for samples");
        List<Sample> samples = findDoableSampleInCloud();
        
        if (!samples.isEmpty()) {
          System.err.println("Found a sample in the cloud ! Happy :)");
          fsm.connect(samples.get(0).id);
          return;
        }
      }
      
      if (me.carriedSamples.size() > 0) {
        // ditch the samples
        System.err.println("Ditch a sample in the hope of getting one that fit");
        fsm.connect(me.carriedSamples.get(0).id);
        return;
      } else {
        System.err.println("Go to Sample to get a new sample");
        fsm.goTo(Module.SAMPLES);
        return;
      }
    }

    System.err.println("Got at least one way to do sample with molecule "+type.toString());
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
