package c4l.fsm;

import c4l.entities.Module;

public class FSMCenter extends FSMNode {

  FSMCenter(FSM fsm) {
    super(fsm);
  }

  @Override
  public void think() {
    // FIRST ACTION OF THE PLAYER
    // WE CANT GO BACK IN THIS STATE AFTERWARD
    fsm.moveToState(fsm.sample);
  }

  @Override
  public Module module() {
    return Module.START_POS;
  }
}
