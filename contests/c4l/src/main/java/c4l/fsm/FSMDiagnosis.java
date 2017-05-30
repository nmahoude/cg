package c4l.fsm;

import java.util.List;
import java.util.stream.Collectors;

import c4l.Order;
import c4l.entities.Module;
import c4l.entities.Sample;
import c4l.molecule.MoleculeComboInfo;
import c4l.sample.SampleInfo;
import c4l.sample.SampleOptimizer;

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
    Sample firstUnDiscoveredSample = getFirstUnDiscoveredSample();
    if (firstUnDiscoveredSample != null) {
      fsm.connect(firstUnDiscoveredSample.id, "Got an undiscover sample, diag it");
      return;
    }      

    if (findANewCompletableSampleAtDiag_new()) {
      return;
    }
    
    // TODO remove the rest not needed anymore
    if (me.carriedSamples.isEmpty()) {
      System.err.println("No more sample, handle it");
      if (findANewCompletableSampleAtDiag()) {
        return;
      } else {
        fsm.goTo(Module.SAMPLES, "Can't find anything @DIAG, go to sample");
        return;
      }
    }
    
    // TODO check if we can exchange a sample to get better result (2 turns taken)
    
    // check if we can complete a sample (that we just get) from availables molecules in the pool
    MoleculeComboInfo combo = fsm.getBestComboForSamples();
    
    if (!combo.canFinishAtLeastOneSample()) {
      System.err.println("no molecule combination for actual samples, get a new one ?");
     
      if (me.carriedSamples.size() < 3 ) {  
        if (findANewCompletableSampleAtDiag()) {
          return;
        }
      } 
      
      // here, we don't have any completable samples so ditch them
      if (me.carriedSamples.size() > 0) {
        //TODO score the sample difficulty, health and completion before ditching it
        me.carriedSamples.sort(Sample.roiSorter(Order.ASC));
        fsm.connect(me.carriedSamples.get(0).id, "Ditch a sample in the hope of getting one that fit (roi not enough)");
        return;
      } else {
        fsm.goTo(Module.SAMPLES, "Go to Sample to get a new sample, no combination possible even in the cloud");
        return;
      }
    } else {
      // NOTE Here, We have completable samples ! 
      // TODO before getting to the MOLECULES, check if we need more samples
      if (me.carriedSamples.size() < 3 ) {
        if (findANewCompletableSampleAtDiag()) {
          return;
        }
      }
      
      if (combo.neededMoleculeToRealiseCombo() == 0) {
        fsm.goTo(Module.LABORATORY, "filled samples combo, but no molecule needed and no more sample can be fullfilled");
        return;
      } else {
        fsm.goTo(Module.MOLECULES, "Got at least one way to do sample ");
        return;
      }
    }
  }

  /**
   * Find a completable sample in the cloud
   */
  boolean findANewCompletableSampleAtDiag_new() {
    SampleOptimizer optimizer = new SampleOptimizer();
    SampleInfo info = optimizer.optimize(state, me);
    
    System.err.println("Samples combo : "+info);
    
    if (info.samples.size() == 0) {
      if (me.carriedSamples.size() > 0) {
        fsm.connect(me.carriedSamples.get(0).id, "Drop sample, there is no best sample");
        return true;
      } else {
        fsm.goTo(Module.SAMPLES, "no best samples, all dropped, go back to SAMPLES");
        return true;
      }
    } else {
      if (me.carriedSamples.containsAll(info.samples)) {
        // need to go to SAMPLES to get more ?
        if (needToGoToSamples(info)) {
          return true;
        }
        // check if we have to go to molecule ...
        MoleculeComboInfo combo = fsm.getBestComboForSamples();
        if (combo.neededMoleculeToRealiseCombo() > 0) {
          fsm.goTo(Module.MOLECULES, "Got all from best samples and need some molecules, goto molecule");
          return true;
        } else {
          fsm.goTo(Module.LABORATORY, "Go directly to lab, can't get nothing from MOLECULE");
          return true;
        }
      } else {
        if (me.carriedSamples.size() == 3) {
          Sample sample = getOneFromNotInto(me.carriedSamples, info.samples);
          fsm.connect(sample.id, "Remove a sample from our list as it is full");
          return true;
        } else {
          Sample sample = getOneFromNotInto(info.samples, me.carriedSamples);
          fsm.connect(sample.id, "Get a sample from the best list");
          return true;
        }
      }
    }
  }

  /**
   * Check if, based on our carried samples, we need to go to samples to get more (ROI anyone ?)
   * @return
   */
  private boolean needToGoToSamples(SampleInfo info) {
    
    if (me.carriedSamples.size() == 3) {
      return false; // can't get any more samples
    }
    for (int i=0;i<3-me.carriedSamples.size();i++) {
      int points = info.points;
      int turns = 
            Module.distance(Module.DIAGNOSIS, Module.SAMPLES)
          + (3-i)  // pick
          + Module.distance(Module.SAMPLES, Module.DIAGNOSIS)
          + (3-i)  ;//analyse
      
      int expected[] = fsm.sample.getCurrentSampleTakenROI();
      turns += (3-i) * expected[0];
      points +=(3-i) * expected[1];
      
      if (1.0 * points / turns > info.score) {
        // ROI is positive, go get some !
        fsm.goTo(Module.SAMPLES, "ROI is positive, go get some samples");
        return true;
      }
    }
    
    return false;
  }
  private Sample getOneFromNotInto(List<Sample> carriedSamples, List<Sample> bestSamples) {
    for (Sample sample : carriedSamples) {
      if (bestSamples.indexOf(sample) == -1) return sample;
    }
    return null;
  }
  /**
   * Find a completable sample in the cloud
   */
  boolean findANewCompletableSampleAtDiag() {
    List<Sample> samples = findDoableSampleInCloud();
    
    // TODO why is this better without ???
    // samples = samples.stream().filter(sample -> sample.health>1).collect(Collectors.toList());
    
    if (samples.isEmpty()) {
      // no completable sample found
      return false;
    } 

    if (chooseSampleToCompleteScienceProject(samples)) {
      return true;
    } 
    // TODO this is a temporary solution to force bot to get samples for scienceProject
    if (chooseSampleToApproachScienceProject(samples)) {
      return true;
    } 
    if (chooseSampleWithBetterROI(samples)) {
      return true;
    } 
    
    System.out.println("YOU SHOULD NOT PASS (HERE)");
    return false; // should not come here
  }


  private boolean chooseSampleWithBetterROI(List<Sample> samples) {
    if (!samples.isEmpty()) {
      //TODO find a better approach than roi ?
      samples.sort(Sample.roiSorter(Order.DESC));
      fsm.connect(samples.get(0).id, "Found a sample in the cloud ! Happy :)");
      return true;
    }
    return false;
  }
  
  /**
   * Choose a sample to get closed to science project completion
   */
  private boolean chooseSampleToApproachScienceProject(List<Sample> samples) {
    Sample best = null;
    int bestDist = state.distanceToScienceProjects(me, new int[] {0, 0, 0, 0, 0});// test against no gain (current situation)
    for (Sample sample : samples) {
      int dist = state.distanceToScienceProjects(me, sample.gain);
      if (dist < bestDist) {
        bestDist = dist;
        best = sample;
      }
    }
    if (best != null) {
      fsm.connect(best.id, "Choose sample to approach ScienceProject with dist "+bestDist);
      return true;
    }
    return false;
  }
  
  /**
   * choose a sample to complete the science project
   * @param samples
   * @return
   */
  boolean chooseSampleToCompleteScienceProject(List<Sample> samples) {
    List<Sample> fillingScienceProjectSamples = findSamplesFillingAScienceProject(samples);
    if (!fillingScienceProjectSamples.isEmpty()) {
      samples.sort(Sample.moleculeNeededSorter(me, Order.ASC));
      fsm.connect(fillingScienceProjectSamples.get(0).id, "WOOT can solve a science project");
      return true;
    }
    return false;
  }
  
  List<Sample> findSamplesFillingAScienceProject(List<Sample> samples) {
    return samples.stream().filter(sample -> {
      return state.distanceToScienceProjects(me, sample.gain) == 0;
    }).collect(Collectors.toList());
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
