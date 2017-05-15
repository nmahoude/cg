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
    if (me.totalExpertise< 6) {
      fsm.connect(1, "go fast at start (copied on Agade strategy");
      return;
    }

    // TODO check in the cloud, if there is a lot of rank2 and we can't do them it means something
    if (me.totalCarried > 8) {
      // TODO play on the xp ? maybe rank1 is too absolute
      if (me.totalExpertise < 12) {
        fsm.connect(1, "Full molecule, on prend des rank 1 pour debloquer");
        return;
      } else {
        fsm.connect(2, "Full molecule, on prend des rank 2 pour debloquer parce que xp suffisante");
        return;
      }
    }
    
    if (me.totalExpertise < 12) {
      fsm.connect(2, "Always get 2, not enough XP");
      return;
    } else {
        fsm.connect(3, "Rand say take rank 3");
        return;
    }
  }


  @Override
  public Module module() {
    return Module.SAMPLES;
  }

}
