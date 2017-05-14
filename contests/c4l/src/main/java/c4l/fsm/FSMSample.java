package c4l.fsm;

import c4l.Player;
import c4l.entities.Module;
import c4l.entities.Sample;

public class FSMSample extends FSMNode {
  FSMSample(FSM fsm) {
    super(fsm);
  }
  
  @Override
  public void think() {
    if (fsm.me.carriedSamples.size() < 3) {
      getSomeSamples();
    } else {
      fsm.goTo(Module.DIAGNOSIS, "Filled the sample to analyse, -> @DIAG");
    }
  }
  
  
  private void getSomeSamples() {
    System.err.println("Get some samples");
    
    if (me.target == Module.DIAGNOSIS) {
      // check if there is an available sample that can fit our molecules
      Sample best = null;
      for (Sample sample : state.availableSamples) {
        if (me.hasMolecules(sample)) {
          if (best == null || best.health < sample.health) {
            best = sample;
          }
        }
      }
      if (best != null) {
        System.err.println("Found a perfect match");
        best.debug();
        if (me.target == Module.DIAGNOSIS) {
          fsm.connect(best.id, " Found a perfect match in DIAG, get it");
        } else {
          fsm.goTo(Module.DIAGNOSIS, "Found a perfect match in DIAG, go get it");
        }
        return;
      }

      System.err.println("No perfect match, check if there is enough total molecules for the sample ");
      best = null;
      double bestScore = Double.NEGATIVE_INFINITY;
      for (Sample sample : state.availableSamples) {
        if (me.isThereEnoughMoleculeForSample(state, sample)) {
          double score = sample.score(me, state);
          if (bestScore < score) {
            best = sample;
            bestScore = score;
          }
        }
      }
      if (best != null) {
        System.err.println("Found a plausible match");
        best.debug();
        if (me.target == Module.DIAGNOSIS) {
          fsm.connect(best.id, "Found a plausible match, get it");
        } else {
          fsm.goTo(Module.DIAGNOSIS, "Found a plausible match, @DIAG to get it");
        }
        return;
      }
      System.err.println("Found no match in the DIAG samples, goto SAMPLES now");
    }

    // check to go back to SAMPLES
    if (me.target != Module.SAMPLES) {
      fsm.goTo(Module.SAMPLES, "Go back to @SAMPLE");
      return;
    }
    System.err.println("we are at SAMPLES, get a sample");
    if (me.totalExpertise < 5) {
      fsm.connect(2, "Always get 2, not enough XP");
    } else {
//      if (state.robots[1].score - me.score >= 20) {
//        fsm.connect(3, "We are behind, take some risks");
//        return;
//      }
      System.err.println("rank 2 or 3, I don't care");
      if (Player.rand.nextBoolean()) {
        fsm.connect(2, "Rand say take rank 2");
        return;
      } else {
        fsm.connect(3, "Rand say take rank 3");
        return;
      }
    }

//    if (me.totalExpertise < 0) {
//      System.err.println("Noob, get a 1");
//      fsm.connect(1);
//      return;
//    } else if (me.totalExpertise < 4) {
//      System.err.println("Not enough expertise, get a 2");
//      fsm.connect(2);
//      return;
//    } else {
//      System.err.println("rank 2 or 3, I don't care");
//      if (Player.rand.nextBoolean()) {
//        fsm.connect(2);
//        return;
//      } else {
//        fsm.connect(3);
//        return;
//      }
//    }
  }


  @Override
  public Module module() {
    return Module.SAMPLES;
  }

}
