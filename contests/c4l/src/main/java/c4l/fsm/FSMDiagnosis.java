package c4l.fsm;

import c4l.entities.Module;

public class FSMDiagnosis extends FSMNode {
  FSMDiagnosis(FSM fsm) {
    super(fsm);
  }
  @Override
  public void think() {
    
  }
  @Override
  public Module module() {
    return Module.DIAGNOSIS;
  }

}
