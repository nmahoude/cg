package c4l.fsm;

import c4l.entities.Module;

public class FSMSample extends FSMNode {
  public static final int TRANSITION_WAIT           = 0;
  public static final int TRANSITION_GET_RANK_1     = 1;
  public static final int TRANSITION_GET_RANK_2     = 2;
  public static final int TRANSITION_GET_RANK_3     = 3;
  public static final int TRANSITION_GOTO_DIAG      = 4;
  public static final int TRANSITION_GOTO_MOLECULE = 5;
  public static final int TRANSITION_GOTO_LAB       = 6;
  
  double proba[] = new double[7];
  
  FSMSample(FSM fsm) {
    super(fsm);
  }
  
  @Override
  public void think() {
    // state i can be
    /**
     * bagFullfillness       0 -> 3
     * xpLevel               0 -> 2
     * diagInterstingSamples 0 -> 3
     * sampleCompletableAuto 0 -> 3
     * 
     */
    // what i can do :
    /*
     * WAIT
     * GET RANK 1
     * GET RANK 2
     * GET RANK 3
     * GOTO DIAG
     * GOTO MOLECULE
     * GOTO LAB
     */
    for (int i=0;i<proba.length;i++) {
      proba[i] = 0.0;
    }
    
    
    double carriedSamples = fsm.me.carriedSamples.size();
    
    
    proba[TRANSITION_WAIT] = 0.0;
    proba[TRANSITION_GET_RANK_1] = 0.0;
    proba[TRANSITION_GET_RANK_2] = 0.0;
    proba[TRANSITION_GET_RANK_3] = 0.0;
    proba[TRANSITION_GOTO_DIAG ] = carriedSamples / 3.0 ;
    proba[TRANSITION_GOTO_MOLECULE] = 0.0;
    proba[TRANSITION_GOTO_LAB] = 0.0;
    
    double wait = 0.0; // never wait at SAMPLE
    double getRank1 = 0.0;
    
    if (fsm.me.carriedSamples.size() < 3) {
      getSomeSamples();
    } else {
      fsm.goTo(Module.DIAGNOSIS, "Filled the sample to analyse, -> @DIAG");
    }
  }
  
  
  private void getSomeSamples() {
    if (me.totalExpertise + me.carriedSamples.size() < 6) {
      fsm.connect(1, "go fast at start (copied from Agade strategy");
      return;
    } 

    // TODO check in the cloud, if there is a lot of rank2 and we can't do them it means something
    if (me.totalCarried > 8 && me.carriedSamples.isEmpty()) {
      // TODO play on the xp ? maybe rank1 is too absolute
      if (me.totalExpertise < 6) {
        fsm.connect(1, "Full molecule, on prend des rank 1 pour debloquer");
        return;
      } else {
        fsm.connect(2, "Full molecule, on prend des rank 2 pour debloquer parce que xp suffisante");
        return;
      }
    }
    
    if (me.totalExpertise + me.carriedSamples.size() < 12) {
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

  /**
   * Rank 1 : 5 -> 4.0
     Rank 2 : 6 -> 18.620689655172413
     Rank 3 : 10 -> 40.0
   * 
   * Warning, take expertise into account for molecule cost
   * @return { moleculeCost, points}
   */
  // TODO review the calcul of moleculeTaken + how it is handled
  public int[] getCurrentSampleTakenROI() {
    if (me.totalExpertise < 6) {
      return new int[] { 5- me.totalExpertise/5,  4 };
    } else if (me.totalExpertise < 12) {
      return new int[] { 6- me.totalExpertise/5, 18 };
    } else {
      return new int[] { 10 - me.totalExpertise/5, 40};
    }
  }

}
