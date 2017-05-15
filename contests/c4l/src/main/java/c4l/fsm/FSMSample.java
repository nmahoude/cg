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

    if (me.totalCarried > 8) {
      // TODO play on the xp ? maybe rank1 is too absolute
      fsm.connect(1, "Full molecule, on prend des rank 1 pour debloquer");
      return;
    }
    
    if (me.totalExpertise < 5) {
      fsm.connect(2, "Always get 2, not enough XP");
      return;
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
